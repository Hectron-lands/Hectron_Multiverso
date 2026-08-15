#!/bin/bash
set -e
echo "HECTRON MULTIVERSO - Terraform Init"
cd terraform
terraform init
terraform validate