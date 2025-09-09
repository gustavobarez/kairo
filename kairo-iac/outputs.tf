output "beanstalk_environment_url" {
    value = aws_elastic_beanstalk_environment.kairo_env.cname
}

output "rds_instance_address" {
    value = aws_db_instance.kairo_db.address
}