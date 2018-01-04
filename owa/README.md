# Bed Management OWA
> Bed management OpenMRS OWA is a application for managing admission locations (Add/Edit/Delete/listing wards/beds).

## Development
#### Local Setup Instructions
1. Move into the owa directory
```bash
cd openmrs-module-bedmanagement/owa
```

2. Install the dependencies
```bash
$ yarn install
```

3. Build & compile code
```bash
$ yarn webpack
$ yarn build-webpack # pack with Compress for production 
```
- This will create app.js & vendor.js at path owa/app/build

4. Install open-web-apps-module module in your local OpenMRS if it not already installed.
- You can download from : https://addons.openmrs.org/#/show/org.openmrs.module.open-web-apps-module
- For more details: https://wiki.openmrs.org/display/docs/Open+Web+Apps+Module?src=contextnavpagetreemode

4. Deploy
- For development it would be better to symbolic link the OWA app to the source code. 
GoTo openmrs > amimin > Settings and check `App folder path`.
```bash
ln -s openmrs-module-bedmanagement/owa/app [App folder path]/bedmanagement
```
- Or you can simply zip /app folder and then upload at openmrs > amimin > Manage Apps

5. For Testing 
```bash
$ yarn test
$ yarn test-watch  # if you want run test in watch mode
```
- If you have changes in UI and render properly as expected, then you can regenerate snapshots
```bash
$ yarn test --u
```
- If you want to run specific test only
```bash
$ yarn test <regexForTestFiles>
```