# Release Notes for GitHub 
cat << EOF > post.json
{
  "name": "v$BRANCH_NAME",
  "body": "# Kaltura Player React Native \n https://www.npmjs.com/package/kaltura-player-rn/v/$BRANCH_NAME",
  "tag_name": "v$BRANCH_NAME"
}
EOF

# echo "BRANCH_NAME is = v$BRANCH_NAME"

POST_URL=https://api.github.com/repos/$GITHUB_REPOSITORY/releases/generate-notes

curl $POST_URL -X POST -H "Accept: application/vnd.github+json" -H "Content-Type: application/json" -H "Authorization: Bearer $GITHUB_TOKEN" -d@post.json #--include