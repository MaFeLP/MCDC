# Advancement Data

As bukkit does not provide a good way to get the name of an advancement, you
need to specify the names yourself. This is done with a json file in the
plugins folder. This page explains, how to create such a file from a Minecraft
Client installation on a Unix-Like system _(tested on Linux)_.

If you do not have such a system, you can also download a preconfigured file
from the list below, or install a package from the releases, as there it has 
the config file bundled.

- [1.18](https://mafelp.github.io/MCDC/download/advancements/1-18.json)

Please see [Place the config file](#place-the-config-file), to see where to put
the downloaded file.

## Prerequisites
- [jq](https://stedolan.github.io/jq/)
- Access to a `.minecraft` folder of a Minecraft client

## Get available languages
1. Open a terminal window.
2. Change the location to your `.minecraft` folder (on Linux and macOS standard
  installations: `cd ~/.minecraft`)
3. Change the location to the assets folder: `cd assets`
4. List available versions with: `ls indexes`
5. List available languages with the following command.
  - Replace `1.18.json` (at the start of the command) with the file name of your version, you identified in step 4
  - Replace `'en'` (at the end of the command) with the search term for the language. Leave blank to list all languages.

```shell
cat "indexes/1.18.json" | jq '.objects' | grep 'minecraft\/lang\/*' | cut -c 19- | sed -e 's/.json": {/ /g' | grep 'en'
```

Example output:
```shell
$ cat "indexes/1.18.json" | jq '.objects' | grep 'minecraft\/lang\/*' | cut -c 19- | sed -e 's/.json": {/ /g' | grep 'en'
en_au 
en_ca 
en_gb 
en_nz 
en_pt 
en_ud 
enp 
enws 
io_en 
jbo_en
```

## Get the config file for you language
1. Run the following command to get the hash of the language file:
  - Replace `1.18.json` (at the start of the command) with the file name of your version, the same as you did with the command before.
  - Replace `en_gb` (at the end of the command) with the language, you chose in [Get available languages](#get-available-languages)

```shell
cat "indexes/1.18.json" | jq '.objects."minecraft/lang/en_gb.json"'
```

Example output:

```shell
$ cat "indexes/1.18.json" | jq '.objects."minecraft/lang/en_gb.json"'

{
  "hash": "58df72ec35741ddb5a1df68a078cd6af04de8989",
  "size": 322264
}
```

## Create the config file
1. Run the following commands
  - In the first line replace the value after the `=` sign with the hash from the previous step.

```shell
HASH="58df72ec35741ddb5a1df68a078cd6af04de8989"
echo -n "{" > /tmp/advancements.json
cat "objects/$(echo $HASH | cut -c '-2')/$HASH" | grep '    "advancements.' | sed -e 's/    "advancements./"/g' | tr '\n' ' ' | tr '\n' ' ' >> /tmp/advancements.json
echo "}" >> /tmp/advancements.json
sed -i 's/, }/}/g' /tmp/advancements.json
cat "/tmp/advancements.json" | jq > "/tmp/advancements_new.json"
mv "/tmp/advancements_new.json" "/tmp/advancements.json"
```

The config file has been created at `/tmp/advancements.json`. You can now move
it, for example to your Desktop with the following command:

```shell
mv "/tmp/advancements.json" "~/Desktop/advancements.json"
```

## Place the config file
You now need to place your `advancements.json` file into the right location of
your server.

This is the following directory:

```
YOUR SERVER DIRECTORY
|- plugins
  |- MCDC
    |- advancements.json
```

