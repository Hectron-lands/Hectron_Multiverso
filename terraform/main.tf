terraform {
  required_version = ">= 1.5.0"
  required_providers {
    google = { source = "hashicorp/google", version = "~> 5.0" }
  }
  backend "gcs" {
    bucket = "abadalabs-hectron-terraform-state"
    prefix = "terraform/state/hectron"
  }
}
provider "google" {
  project = var.gcp_project_id
  region  = var.gcp_region
}
module "gke" {
  source  = "terraform-google-modules/kubernetes-engine/google"
  version = "~> 30.0"
  project_id = var.gcp_project_id
  name = "hectron-cluster"
  region = var.gcp_region
  node_pools = [{
    name = "hectron-pool"
    machine_type = "e2-medium"
    min_count = 1
    max_count = 5
    initial_node_count = 2
    disk_size_gb = 100
  }]
}