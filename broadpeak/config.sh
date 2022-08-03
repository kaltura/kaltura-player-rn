BPK_NAME=$1
BPK_PASSWORD=$2

cat << EOF > bpkConfig.json
{
  "userName": "$BPK_NAME",
  "password": "$BPK_PASSWORD"
}
EOF
