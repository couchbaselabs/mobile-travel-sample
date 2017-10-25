# Deploy Workshop Vagrantfile

System for quickly and painlessly provisioning Couchbase Server virtual machines across multiple Couchbase versions and OS's.

Based on https://github.com/couchbaselabs/vagrants, with some things added and many things taken away to streamline.

## Starting a Couchbase cluster

If Vagrant and VirtualBox are installed, it is very easy to get started with a cluster of one Sync Gateway and one Couchbase Server node. This can then be expanded to a full cluster.

See this blog post for more info: http://nitschinger.at/A-Couchbase-Cluster-in-Minutes-with-Vagrant-and-Puppet

Just change into the appropriate directory and call `vagrant up`. Everything else will be done for you, but you need
internet access.

### Automatic and Optional Nodes

By default, the basic `vagrant up` command will start one Couchbase Server and one Sync Gateway node. There are additional nodes (by default 2x Couchbase Server, 1x Sync Gateway, and 1x NGINX) which can be started by specifying their name (e.g. `vagrant up node3-cb`). The numbers of these optional nodes are also tuneable with environment variables.

### Environment Variables

There are a number of environment variables that can be used to tune and tweak the nodes:

|Environment Variable  |Default   |Description|
|----------------------|----------|-----------|
|VAGRANT_CPUS          |`1`       |Number of vCPUs to allocate to each VM|
|VAGRANT_RAM           |`1024`    |RAM in MB to allocate to each VM|
|VAGRANT_OS            |`centos7` |OS to use for each VM. Available options are `centos7` and `ubuntu14`|
|VAGRANT_CB_EXTRA_NODES|`2`         |Number of Couchbase Server nodes to allow provisioning by name. **N.B. If you need to change this, ensure that any VMs that have been previously created by them are destroyed before changing.**|
|VAGRANT_SG_EXTRA_NODES|`1`         |Number of Couchbase Server nodes to allow provisioning by name. **N.B. If you need to change this, ensure that any VMs that have been previously created by them are destroyed before changing.**|


### IP Addresses

The nodes will have IP addresses based on the pattern `10.150.150.1n` where `nn` is the node number (zero padded to two digits).

### Hostnames

The hostname of the VM will be set based on the version of Couchbase Server and of the OS selected, e.g.:

    `node1-ubuntu14.vagrants`
    `node2-centos7.vagrants`

### Configs

Both Sync Gateway and NGINX can be configured by config file. To facilitate this, the respective puppet scripts will copy in appropriately named files from the host and restart the service:

|Service     |Config File Name   |Destination|
|------------|-------------------|-----------|
|Sync Gateway|`sync_gateway.json`|`/home/sync_gateway/sync_gateway.json`|
|NGINX       |`nginx.conf`       |`/etc/nginx/conf.d/sync_gateway_nginx.conf`|