# ===
# Install and Run NGINX
# ===

if $operatingsystem == 'Ubuntu'{
  exec { "apt-get update":
	     path => "/usr/bin",
       before => Package["nginx"]
  }
}
elsif $operatingsystem == 'CentOS'{
  package { 'epel-release':
    ensure => installed,
    before => Package["nginx"]
  }
  case $::operatingsystemmajrelease {
    '5', '6': {
      # Ensure firewall is off (some CentOS images have firewall on by default).
      service { "iptables":
        ensure => "stopped",
        enable => false
      }
    }
    '7': {
      # This becomes 'firewalld' in RHEL7'
      service { "firewalld":
        ensure => "stopped",
        enable => false
      }
    }
  }

# Install pkgconfig (not all CentOS base boxes have it).
package { "pkgconfig":
    ensure => present,
  }
}

# Install Sync Gateway
package { "nginx":
    provider => $operatingsystem ? {
        Ubuntu => apt,
        CentOS => yum,
    },
    ensure => present,
}

# Copy config if one supplied
exec { "sync_gateway-config":
    command => "/bin/cp /vagrant/nginx.conf /etc/nginx/conf.d/sync_gateway_nginx.conf",
    returns => [0,1],
    notify => Service["nginx"],
    require => Package["nginx"]
}

# Ensure the service is running
service { "nginx":
	ensure => "running"
}
