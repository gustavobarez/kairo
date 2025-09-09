set -e

echo "🔧 FORÇANDO LIMPEZA DO ESTADO TERRAFORM"
echo "Este script vai remover recursos problemáticos do estado sem deletá-los"
echo ""

# Fazer backup do estado atual
if [ -f "terraform.tfstate" ]; then
    BACKUP_FILE="terraform.tfstate.backup.$(date +%Y%m%d_%H%M%S)"
    cp terraform.tfstate "$BACKUP_FILE"
    echo "✅ Backup criado: $BACKUP_FILE"
fi

# Listar recursos no estado
echo ""
echo "📋 Recursos atuais no estado:"
terraform state list 2>/dev/null || echo "Nenhum recurso encontrado"

echo ""
echo "🗑️  Removendo recursos problemáticos do estado..."

# Array de recursos para remover (sem tentar deletar)
RESOURCES_TO_REMOVE=(
    "aws_security_group.rds_sg"
    "aws_security_group.eb_sg"
    "aws_security_group_rule.beanstalk_to_rds"
    "aws_db_instance.kairo_db"
    "aws_db_subnet_group.kairo_subnet_group"
    "aws_elastic_beanstalk_environment.kairo_env"
    "aws_elastic_beanstalk_application.kairo_app"
)

for resource in "${RESOURCES_TO_REMOVE[@]}"; do
    if terraform state list | grep -q "^${resource}$" 2>/dev/null; then
        echo "   Removendo ${resource} do estado..."
        terraform state rm "$resource" 2>/dev/null || true
    else
        echo "   ${resource} não encontrado no estado"
    fi
done