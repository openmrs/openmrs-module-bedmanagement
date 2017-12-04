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

4. Deploy
- For development it would be better to link the OWA app to the source code.
```bash
ln -s openmrs-module-bedmanagement/owa/app [path to openmrs owa]/bedmanagement
```
- Or you can simply zip /app folder and then upload at openmrs > amimin > Manage Apps

5. For Testing 
```bash
$ yarn test
$ yarn test-watch  # if you want run test in watch mode

```