#!/usr/bin/env bash

if [ -z "$2" ]
then
  cat <<EOF
  Packaging Script for MCDC

  Options:
  --------

  Arguments:
  ----------
  1. Path to Minecraft folder (example: "$HOME/.minecraft")
  2. Version to package (example: "0.12.0-beta")

  Example:
  --------
  bash-scripts/package.sh "$HOME/.minecraft" "0.12.0-beta"
EOF
  exit 1
fi

echo ":: Building plugin with maven..."
mvn clean package verify

echo ":: Creating packaging directories"
mkdir -pv /tmp/package/plugins/mcdc
echo "==> Directory '/tmp/package/plugins/mcdc' and parents has been created!"
echo ":: Copying files"
cp -v defaultConfiguration.yml /tmp/package/plugins/mcdc/config.yml
cp -v LICENSE /tmp/package/plugins/mcdc/LICENSE
cp -v "target/mcdc-$2.jar" /tmp/package/plugins/

# Create the config file for the advancements
(
  echo ":: Generating advancements.json file"
  echo "==> Using Minecraft directory $1"
  echo "==> Using Minecraft version 1.18"
  echo "==> Using language: en_gb"
  cd "$1/assets/" || exit 1
  HASH=$(jq -r '.objects."minecraft/lang/en_gb.json".hash' < "indexes/1.18.json")
  DIRECTORY=$(echo "$HASH" | cut -c '-2')
  echo "==> Hash for file is: $HASH"
  echo "==> Directory of Hash file is: $DIRECTORY"
  echo "==> Extracting all advancements from the file..."
  echo -n "{" > /tmp/advancements.json
  grep '    "advancements.' < "objects/$DIRECTORY/$HASH" | sed -e 's/    "advancements./"/g' | tr '\n' ' ' | tr '\n' ' ' >> /tmp/advancements.json
  echo "}" >> /tmp/advancements.json
  sed -i 's/, }/}/g' /tmp/advancements.json
  jq > "/tmp/advancements_new.json" < "/tmp/advancements.json"
  mv "/tmp/advancements_new.json" "/tmp/package/plugins/mcdc/advancements.json"
  echo "==> Advancement configuration created at '/tmp/package/plugins/mcdc/advancements.json'"
)

# Create the packages
(
  cd /tmp/package/ || exit 1
  echo ":: Creating packages"
  echo "==> Creating zip package"
  zip -r -9 "mcdc-$2-package.zip" plugins
  echo "==> Creating tar.gz package"
  tar czfv "mcdc-$2-package.tar.gz" plugins
  echo ":: Creating checksums"
  cat > "ReleaseMessage.md" <<EOF
## What's Changed
- 🐞
- ➡️
- ➕

## Which files do I have to download?
It depends. If you want it easy, download either \`mcdc-$2-package.tar.gz\` (for Linux and MacOS
or \`mcdc-$2-package.zip\` (for Windows). Unzip this file into your server directory.
If you want to install everything manually, download the files needed. The jar is the plugin, the config contains
default values and in the \`advancements.json\` there are default translationf or Minecraft\'s advancements.

## Installation
See [GitHub Pages](https://mafelp.github.io/MCDC/installation)

## Configuration
See [GitHub Pages](https://mafelp.github.io/MCDC/configuration)

## Commands
- Minecraft
  - \`/link\`
  - \`/token\`
  - \`/config\`
  - \`/account\`
  - \`/unlink\`
  - \`/whisper\`
    - \`/dcmsg\`
- Discord
  - \`/help\`
  - \`/createChannel\`
  - \`/createRole\`
  - \`/link\`
  - \`/setup\`
  - \`/unlink\`
  - \`/whisper\`
    - \`/mcmsg\`

<!--TODO Add old tag-->
## Changes
- See the included Changelog
- **Full Changelog**: https://github.com/MaFeLP/MCDC/compare/vX.XX.X-beta...v$2

## Checksums:
<details>
<summary>Click to expand</summary>

- mcdc-$2-package.tar.gz
  - MD5: \`$(md5sum "mcdc-$2-package.tar.gz" | cut -d ' ' -f1)\`
  - SHA1: \`$(sha1sum "mcdc-$2-package.tar.gz" | cut -d ' ' -f1)\`
  - SHA256: \`$(sha256sum "mcdc-$2-package.tar.gz" | cut -d ' ' -f1)\`
  - SHA512: \`$(sha512sum "mcdc-$2-package.tar.gz" | cut -d ' ' -f1)\`
- mcdc-$2-package.zip
  - MD5: \`$(md5sum "mcdc-$2-package.zip" | cut -d ' ' -f1)\`
  - SHA1: \`$(sha1sum "mcdc-$2-package.zip" | cut -d ' ' -f1)\`
  - SHA256: \`$(sha256sum "mcdc-$2-package.zip" | cut -d ' ' -f1)\`
  - SHA512: \`$(sha512sum "mcdc-$2-package.zip" | cut -d ' ' -f1)\`
- mcdc-$2.jar
  - MD5: \`$(md5sum "plugins/mcdc-$2.jar" | cut -d ' ' -f1)\`
  - SHA1: \`$(sha1sum "plugins/mcdc-$2.jar" | cut -d ' ' -f1)\`
  - SHA256: \`$(sha256sum "plugins/mcdc-$2.jar" | cut -d ' ' -f1)\`
  - SHA512: \`$(sha512sum "plugins/mcdc-$2.jar" | cut -d ' ' -f1)\`

</details>
EOF
  if [ -z "$EDITOR" ]
  then
    nano "ReleaseMessage.md"
  else
    "$EDITOR" "ReleaseMessage.md"
  fi
  echo "Done!"
)

gh release view "$2"
echo ":: Uploading assets to GitHub"
gh release upload "v$2" "/tmp/package/plugins/mcdc/advancements.json" "/tmp/package/plugins/mcdc/config.yml" "/tmp/package/plugins/mcdc/LICENSE" "/tmp/package/plugins/mcdc-$2.jar" "/tmp/package/mcdc-0.12.0-beta-package.tar.gz" "/tmp/package/mcdc-0.12.0-beta-package.zip"
echo "Printing final Release Message:"
cat "/tmp/package/ReleaseMessage.md"
