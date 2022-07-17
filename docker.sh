GREEN="\033[1;32""m"
NC="\033[0m"

echo "${GREEN} > docker login ${NC}"
docker login
echo "${GREEN} > stop container ${NC}"
docker stop dietfriends
echo "${GREEN} > remove container ${NC}"
docker rm dietfriends
echo "${GREEN} > pull latest image ${NC}"
docker pull azqazq195/dietfriends
echo "${GREEN} > run container ${NC}"
docker run -d -p 9999:9999 --name=dietfriends --restart=always -v /var/log/dietfriends:/workspace/logs -v /var/run/docker.sock:/var/run/docker.sock -v dietfriends_data:/data azqazq195/dietfriends --memory=2g
