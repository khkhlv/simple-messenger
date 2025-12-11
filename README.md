# devops-project

ansible-playbook \
-i 192.168.66.2, \
-u khkhlv \
--ask-pass \
--ask-become-pass \
-e 'ansible_ssh_common_args="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"' \
playbook.yml