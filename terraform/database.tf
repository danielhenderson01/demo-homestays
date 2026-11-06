ephemeral "aws_secretsmanager_secret_version" "db" {
  secret_id = var.hs_db_secret_arn
}

resource "aws_db_subnet_group" "database" {
  name       = "${var.hs_project_name}-db-subnets"
  subnet_ids = aws_subnet.private[*].id
}

resource "aws_db_instance" "database" {
  identifier                = "${var.hs_project_name}-postgres"
  engine                    = "postgres"
  engine_version            = "18"
  instance_class            = "db.t4g.micro"
  allocated_storage         = 20
  storage_encrypted         = true
  db_name                   = var.hs_db_name
  username                  = var.hs_db_username
  password_wo               = jsondecode(ephemeral.aws_secretsmanager_secret_version.db.secret_string)["password"]
  password_wo_version       = var.hs_db_password_version
  port                      = 5432
  publicly_accessible       = false
  db_subnet_group_name      = aws_db_subnet_group.database.name
  vpc_security_group_ids    = [aws_security_group.database.id]

  skip_final_snapshot       = false
  final_snapshot_identifier = "${var.hs_project_name}-database-final"
}
