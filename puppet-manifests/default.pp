Exec { path => [ "/bin/", "/sbin/" , "/usr/bin/", "/usr/sbin/" ] }

include 'stdlib'
include 'apt'
include 'staging'
include 'erlang'

class {'::mongodb::globals':
  bind_ip => ['0.0.0.0'],
}->
class {'::mongodb::server':
  port    => 27017,
  verbose => true,
}

class {'rabbitmq':
    port                 => '5672',
    management_port      => '55672',
    admin_enable         => true,
    env_config_path      => '/etc/rabbitmq/rabbitmq-env.config',
    delete_guest_user    => true,
}
rabbitmq_user { 'bulbs_core':
    admin    => true,
    password => 'bulbs_core',
}
#rabbitmq_vhost { '/':
#  ensure => present,
#}
rabbitmq_user_permissions { 'bulbs_core@/':
    configure_permission => '.*',
    read_permission      => '.*',
    write_permission     => '.*',
}
