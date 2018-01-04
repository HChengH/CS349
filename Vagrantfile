# -*- mode: ruby -*-
# vi: set ft=ruby :
Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"
  config.vm.provider "virtualbox" do |vb|
    vb.name = "CS349"
    vb.gui = true
    vb.memory = "2048"
  end

  config.vm.synced_folder "assignments/", "/home/ubuntu/cs349/assignments"
  config.vm.synced_folder "examples/", "/home/ubuntu/cs349/examples"
  config.vm.provision "shell", inline: <<-SHELL
    apt-get -y update
    apt-get -y install lubuntu-desktop openjdk-8-jdk g++ libx11-dev zip git
    passwd -d -u ubuntu
    reboot
  SHELL
end