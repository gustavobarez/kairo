terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# =============================================================================
# DATA SOURCES - Busca informações da sua conta AWS
# =============================================================================

# Pega as informações da sua VPC padrão
data "aws_vpc" "default" {
  default = true
}

# Pega as sub-redes da VPC padrão, mas filtra APENAS as que são compatíveis
# com a instância t3.micro, conforme a mensagem de erro da AWS.
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }

  filter {
    name   = "availability-zone"
    values = ["us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d", "us-east-1f"]
  }
}

# =============================================================================
# IAM ROLES - Permissões para o Elastic Beanstalk (CRUCIAL)
# =============================================================================

# 1. Função para o serviço do Elastic Beanstalk
resource "aws_iam_role" "eb_service_role" {
  name = "aws-elasticbeanstalk-service-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action    = "sts:AssumeRole",
        Effect    = "Allow",
        Principal = {
          Service = "elasticbeanstalk.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "eb_service_policy" {
  role       = aws_iam_role.eb_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSElasticBeanstalkService"
}

# 2. Perfil de instância para as máquinas EC2
resource "aws_iam_role" "eb_ec2_role" {
  name = "aws-elasticbeanstalk-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action    = "sts:AssumeRole",
        Effect    = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "eb_ec2_webtier" {
  role       = aws_iam_role.eb_ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSElasticBeanstalkWebTier"
}

resource "aws_iam_instance_profile" "eb_instance_profile" {
  name = "aws-elasticbeanstalk-ec2-role"
  role = aws_iam_role.eb_ec2_role.name
}


# =============================================================================
# NETWORKING - Grupos de Segurança e Regras
# =============================================================================

resource "aws_security_group" "rds_sg" {
  name        = "${var.app_name}-rds-sg"
  description = "Permite acesso a porta do PostgreSQL vindo do Beanstalk"
  vpc_id      = data.aws_vpc.default.id
}

resource "aws_security_group" "eb_sg" {
  name        = "${var.app_name}-eb-sg"
  description = "Permite trafego HTTP de entrada e todo o de saida"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group_rule" "beanstalk_to_rds" {
  type                     = "ingress"
  from_port                = 5432 # Porta do PostgreSQL
  to_port                  = 5432
  protocol                 = "tcp"
  security_group_id        = aws_security_group.rds_sg.id
  source_security_group_id = aws_security_group.eb_sg.id
}

# =============================================================================
# DATABASE - Grupo de Sub-redes e Instância RDS
# =============================================================================

resource "aws_db_subnet_group" "kairo_subnet_group" {
  name       = "${var.app_name}-subnet-group"
  subnet_ids = data.aws_subnets.default.ids
}

resource "aws_db_instance" "kairo_db" {
  identifier             = "${var.app_name}-db"
  allocated_storage      = 20
  engine                 = "postgres"
  engine_version         = "15.7"
  instance_class         = var.db_instance_class
  db_name                = var.db_name
  username               = var.db_user
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.kairo_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  skip_final_snapshot    = true
}

# =============================================================================
# ELASTIC BEANSTALK - Aplicação e Ambiente
# =============================================================================

resource "aws_elastic_beanstalk_application" "kairo_app" {
  name        = var.app_name
  description = "Aplicação de agendamento Kairo"
}

resource "aws_elastic_beanstalk_environment" "kairo_env" {
  name                = "${var.app_name}-env"
  application         = aws_elastic_beanstalk_application.kairo_app.name
  solution_stack_name = "64bit Amazon Linux 2023 v4.6.4 running Corretto 21"

  # Depende explicitamente da criação da regra de SG para garantir a ordem
  depends_on = [aws_security_group_rule.beanstalk_to_rds]

  # --- Configurações do Ambiente ---

  # Associa as Funções IAM criadas (resolve o erro 'Instance Profile')
  setting {
    namespace = "aws:elasticbeanstalk:environment"
    name      = "ServiceRole"
    value     = aws_iam_role.eb_service_role.name
  }

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "IamInstanceProfile"
    value     = aws_iam_instance_profile.eb_instance_profile.name
  }

  # Configura a rede (VPC e Sub-redes filtradas)
  setting {
    namespace = "aws:ec2:vpc"
    name      = "VPCId"
    value     = data.aws_vpc.default.id
  }
  setting {
    namespace = "aws:ec2:vpc"
    name      = "Subnets"
    value     = join(",", data.aws_subnets.default.ids)
  }

  # Configura o Grupo de Segurança
  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "SecurityGroups"
    value     = aws_security_group.eb_sg.id
  }

  # Configura o tipo de instância
  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = "t3.micro"
  }

  # Variáveis de ambiente para a aplicação Spring Boot
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_URL"
    value     = "jdbc:postgresql://${aws_db_instance.kairo_db.endpoint}/${aws_db_instance.kairo_db.db_name}"
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_USERNAME"
    value     = var.db_user
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_PASSWORD"
    value     = var.db_password
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_JPA_HIBERNATE_DDL_AUTO"
    value     = "update"
  }
  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SERVER_PORT"
    value     = "5000"
  }
}