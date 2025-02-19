terraform {
  required_providers {
    yandex = {
      source = "yandex-cloud/yandex"
    }
  }
  required_version = ">= 0.13"
}

provider "yandex" {
  zone = "ru-central1-a"
}

data "yandex_compute_image" "container-optimized-image" {
  family = "container-optimized-image"
}

resource "yandex_compute_instance" "instance-based-on-coi" {
  boot_disk {
    initialize_params {
      image_id = data.yandex_compute_image.container-optimized-image.id
    }
  }
  network_interface {
    subnet_id = "e9bcn9027uc8o5g0iude"
    nat = true
  }
  resources {
    cores = 2
    memory = 2
  }
  metadata = {
    docker-container-declaration = file("${path.module}/declaration.yaml")
    user-data = file("${path.module}/cloud_config.yaml")
  }
}
