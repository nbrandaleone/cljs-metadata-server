# cljs-metadata-server
A program which queries the local metadata server
for GCP Cloud Run.

More info on which data can be queried is located here:
https://cloud.google.com/run/docs/reference/container-contract#sandbox

## Build and Deploy to Cloud Run
gcloud run deploy metadata --allow-unauthenticated --source .


### NOTE
The target directory (which included the executable) was not being copied over to Google Build.
The `gcloud` CLI was dynamically building a `.gcloudignore` file, and the target directory was in the list (due to being listed in `.gitignore`), thus being excluded from the build process.

See: https://cloud.google.com/sdk/gcloud/reference/topic/gcloudignore
Useful command: gcloud meta list-files-for-upload
