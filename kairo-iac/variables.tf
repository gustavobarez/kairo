variable "aws_region" {
    type = string
    default = "us-east-1"
}

variable "app_name" {
    type = string
    default = "kairo"
}

variable "db_name" {
    type = string
    default = "kairodb"
}

variable "db_user" {
    type = string
    default = "kairouser"
}

variable "db_password" {
    type = string
    sensitive = true
}

variable "db_instance_class" {
    type = string
    default = "db.t3.micro"
}