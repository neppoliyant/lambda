resource "digitalocean_droplet" "lambda-1" {
  image = "docker"
  name = "lambda-1"
  region = "sfo1"
  size = "512mb"
  private_networking = false
  ssh_keys = [
    "${var.ssh_fingerprint}"
  ]

  connection {
    user = "root"
    type = "ssh"
    key_file = "${var.pvt_key}"
    timeout = "2m"
  }
}

//output "address_lambda-1" {
//  value = "${digitalocean_droplet.labmda-1.ipv4_address}"
//}
