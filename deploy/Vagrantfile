# System for quickly and painlessly provisioning Couchbase Server virtual machines across multiple Couchbase versions and OS's.
# See README.md for usage instructions

### Variable declarations - FEEL FREE TO EDIT THESE ###
begin
CB_VERSION = "5.0.0"
SG_VERSION = "1.5.0"

VAGRANT_BOXES = { # Vagrant Cloud base boxes for each operating system
  "pre-supplied" => "connect_sv",
  "ubuntu14" => "ubuntu/trusty64",
  "centos7"  => { "box_name" => "puppetlabs/centos-7.0-64-puppet",
                  "box_version" => "1.0.1"
                },
}
# Couchbase Server Version download links
COUCHBASE_RELEASES = "http://packages.couchbase.com/releases"
SYNC_GATEWAY_RELEASES = "#{COUCHBASE_RELEASES}/couchbase-sync-gateway"

COUCHBASE_DOWNLOAD_LINKS = {
  "pre-supplied" => "#{COUCHBASE_RELEASES}/#{CB_VERSION}/couchbase-server-enterprise-#{CB_VERSION}-centos7.x86_64",
  "centos7"    => "#{COUCHBASE_RELEASES}/#{CB_VERSION}/couchbase-server-enterprise-#{CB_VERSION}-centos7.x86_64",
  "ubuntu14"   => "#{COUCHBASE_RELEASES}/#{CB_VERSION}/couchbase-server-enterprise_#{CB_VERSION}-ubuntu14.04_amd64",
}

SYNC_GATEWAY_DOWNLOAD_LINKS = {
  "pre-supplied" => "#{SYNC_GATEWAY_RELEASES}/#{SG_VERSION}/couchbase-sync-gateway-enterprise_#{SG_VERSION}_x86_64",
  "centos7"    => "#{SYNC_GATEWAY_RELEASES}/#{SG_VERSION}/couchbase-sync-gateway-enterprise_#{SG_VERSION}_x86_64",
  "ubuntu14"   => "#{SYNC_GATEWAY_RELEASES}/#{SG_VERSION}/couchbase-sync-gateway-enterprise_#{SG_VERSION}_x86_64",
}

DEFAULT_RAM = 1024
DEFAULT_CPUS = 1
IP_ADDRESS = "10.150.150.1%.2d"
CB_MAN_NODES = 2
SG_MAN_NODES = 1

# Name of the host endpoint to serve as bridge to local network
#  (if not found vagrant will ask the user for each node)
default_bridge = ["wlan0", "wlp1s0"]

### DO NOT EDIT BELOW THIS LINE

unless ENV['VAGRANT_CPUS'].nil? || ENV['VAGRANT_CPUS'] == 0
  num_cpus = ENV['VAGRANT_CPUS'].to_i
else
  if num_cpus.nil?
    num_cpus = DEFAULT_CPUS
  end
end

unless ENV['VAGRANT_OS'].nil?
  unless ["centos7", "ubuntu14", "pre-supplied"].include? ENV['VAGRANT_OS']
    puts "VAGRANT_OS `#{ENV['VAGRANT_OS']}` is invalid."
    puts "Available OSs are `centos7`, `ubuntu14` or `pre-supplied`"
    abort
  else
    $operating_system = ENV['VAGRANT_OS']
  end
else
  $operating_system = "centos7"
end

unless ENV['VAGRANT_CB_EXTRA_NODES'].nil?
  cb_ex_nodes = ENV['VAGRANT_CB_EXTRA_NODES'].to_i
else
  cb_ex_nodes = 0
end

unless ENV['VAGRANT_SG_EXTRA_NODES'].nil?
  sg_ex_nodes = ENV['VAGRANT_SG_EXTRA_NODES'].to_i
else
  sg_ex_nodes = 0
end

unless ENV['VAGRANT_RAM'].nil? || ENV['VAGRANT_RAM'] == 0
  ram_in_MB = ENV['VAGRANT_RAM'].to_i
else
  if ram_in_MB.nil?
    ram_in_MB = DEFAULT_RAM
  end
end

# Check to see if a custom download location has been given, if not use a default value (2.5.0 style)
unless ENV['VAGRANT_CB_DOWNLOAD'].nil?
    $cb_url = ENV['VAGRANT_CB_DOWNLOAD']
else
    $cb_url = COUCHBASE_DOWNLOAD_LINKS[$operating_system]
end
unless ENV['VAGRANT_SG_DOWNLOAD'].nil?
    $sg_url = ENV['VAGRANT_SG_DOWNLOAD']
else
    $sg_url = SYNC_GATEWAY_DOWNLOAD_LINKS[$operating_system]
end

# Generate a hostname template
$hostname = "node%d-#{$operating_system}.vagrants"

### Start the vagrant configuration ###
Vagrant.configure("2") do |config|

  # Define VM properties for each node (for both virtualbox and
  # libvirt providers).
  config.vm.provider :virtualbox do |vb|
    vb.memory = ram_in_MB
    vb.cpus = num_cpus
    vb.customize ["modifyvm", :id, "--ioapic", "on"]
    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "off"]
    vb.linked_clone = true if Vagrant::VERSION >= "1.8.0"
    vb.destroy_unused_network_interfaces = true
  end
  config.vm.provider :libvirt do |libvirt|
    libvirt.memory = ram_in_MB
    libvirt.cpus = num_cpus
  end

  config.vm.synced_folder ENV['HOME'], "/vmhost_home/"

  # Define the vagrant box download location
  if !(VAGRANT_BOXES[$operating_system]["box_url"].nil?)
    config.vm.box_url = VAGRANT_BOXES[$operating_system]["box_url"]
  end

  # Define the vagrant box name
  if !(VAGRANT_BOXES[$operating_system]["box_name"].nil?)
    $box_name = VAGRANT_BOXES[$operating_system]["box_name"]
  else
    $box_name = VAGRANT_BOXES[$operating_system]
  end

  # Define the box version if specified - default to most recent
  if !(VAGRANT_BOXES[$operating_system]["box_version"].nil?)
    $box_version = VAGRANT_BOXES[$operating_system]["box_version"]
  end

  if Vagrant.has_plugin?("vagrant-cachier")
    # Configure cached packages to be shared between instances of the same base box.
    config.cache.scope = :box
  end

  $node_count = 0
  # These nodes are provisioned automatically on `vagrant up`
  configure_nodes(config, "cb", 1, true)
  configure_nodes(config, "sg", 1, true)

  # These nodes will always exist, but need to be named in a command, e.g. `vagrant up node3-cb`
  configure_nodes(config, "cb", CB_MAN_NODES, false)
  configure_nodes(config, "sg", SG_MAN_NODES, false)
  configure_nodes(config, "nginx", 1, false)

  # These nodes will only exist if specified by an env var
  configure_nodes(config, "cb", cb_ex_nodes, false)
  configure_nodes(config, "sg", sg_ex_nodes, false)

end

def configure_nodes(config, product, num_nodes, autostart)
  if num_nodes == 0
    return
  end
  ($node_count + 1).upto($node_count + num_nodes) do |num|
    config.vm.define "node#{num}-#{product}", autostart: autostart do |node|

      # Provision the server itself with puppet
      node.vm.provision "puppet" do |puppet|
        puppet.manifests_path = "." # Define a custom location and name for the puppet file
        if product == "cb"
          puppet.manifest_file = "cb_puppet.pp"
          puppet.facter = { # Pass variables to puppet
            "url" => $cb_url # Couchbase download location
          }
          puts "Couchbase Server:\thttp://#{IP_ADDRESS % num}:8091/"
        elsif product == "sg"
          puppet.manifest_file = "sg_puppet.pp"
          puppet.facter = { # Pass variables to puppet
            "url" => $sg_url # Sync Gateway download location
          }
          puts "Sync Gateway:\t\thttp://#{IP_ADDRESS % num}:4984/"
        elsif product == "nginx"
          puppet.manifest_file = "nginx_puppet.pp"
          puts "NGINX :\t\t\thttp://#{IP_ADDRESS % num}:4984/"
        end

      end

      node.vm.box = $box_name
      if !($box_version.nil?)
        node.vm.box_version = $box_version
      end
      node.vm.network :private_network, :ip => IP_ADDRESS % num

      node.vm.hostname = $hostname % num

      node.vm.provider "virtualbox" do |v|
        if product == "cb"
          v.name = "Couchbase Server #{CB_VERSION} #{$operating_system.gsub '/', '_'} Node #{num}"
        elsif product == "sg"
          v.name = "Sync Gateway #{SG_VERSION} #{$operating_system.gsub '/', '_'} Node #{num}"
        elsif product == "nginx"
          v.name = "NGINX #{$operating_system.gsub '/', '_'} Node #{num}"
        end

      end

      # Postfix a random value to hostname to uniquify it.
      node.vm.provider "libvirt" do |v|
        v.random_hostname = true
      end
    end
  end
  $node_count += num_nodes
end

end
