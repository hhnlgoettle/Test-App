# InteractionRewardingAdsTestApp

An App to showcase the functionality of Interaction Rewarding Ads.

## License
The license can be found in `LICENSE`.

## Deploy to private cdn

requires ssh-access to user `ira` @ `trustmeimansoftware.engineer`

run `rsync -aP --rsync-path="mkdir -p /home/ira/cdn/www/app/ && rsync" ./app-debug.apk ira@trustmeimansoftware.engineer:/home/ira/cdn/www/app/app.apk`