# APOS+

This is a fork of [ORSC APOS](https://gitlab.com/open-runescape-classic/APOS) with extended features. The solution doesn't include scripts ie Scripts directory is empty.
Also, this implementation depends on external libraries. Make sure to install 3rd party dependencies via `restore-libs.cmd` task. These libraries are required to make account reporting work.

## APOS scripts compatibility with APOS+
New changes from APOS repo are manually merged to APOS+ repository. This means when the APOS gets new features they are not immedtialy available in the APOS+. 
Also, if there are new Scripts available for APOS which use some new API methods from the APOS, these scripts might not work unless necessary changes are merged to APOS+.

*Compatible with commit version:* [03 Dec, 2021](https://gitlab.com/open-runescape-classic/APOS/-/commit/0e79dd3108e70e63dea719d3ba428e5924730389)

## Extended features

### Account report
Account report enables the bot to send user data for the preconfigured `APOS+ Report API` at defined intervals.
The following information is sent:
 - Skill levels
 - Inventory items
 - Bank items (only if a executing script has had bank screen visible)

#### How to use
Follow the instructions to set up the [APOS+ Report API](https://github.com/rene-ott/orsc-aposp-report)
Create configuration file and set following values in the `./conf/bot.properties` file:
1) report_api_url - APOS+ Report API report endpoint URL.
2) report_api_key - APOS+ Report API api key, which is necessary to authenticate the request to endpoint.
3) report_interval - The time interval after which the report is passed to the API. Format `hh:mm:ss` where `hh` hours, `mm` minutes and `ss` seconds.

Example configuration:
```properties
report_api_url=http://localhost:80/api/reports/account
report_api_key=hello123
report_interval=00:10:00
```
Start the bot and enable the `Report` checkbox