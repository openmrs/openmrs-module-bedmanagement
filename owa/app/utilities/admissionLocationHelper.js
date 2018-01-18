import pickBy from 'lodash.pickby';

export default class AdmissionLocationHelper {
    /**
     * Return admission location by uuid from list of admission locations
     *
     * @param admissionLocations
     * @param admissionLocationUuid
     * @returns {object}
     */
    getAdmissionLocation = (admissionLocations, admissionLocationUuid) => {
        return typeof admissionLocations[admissionLocationUuid] !== 'undefined'
            ? admissionLocations[admissionLocationUuid]
            : null;
    };

    /**
     * Return higher level admission locations from list of admission locations
     *
     * @param admissionLocations
     * @returns {object}
     */
    getHigherLevelAdmissionLocations = (admissionLocations) => {
        return pickBy(admissionLocations, (admissionLocation) => {
            return admissionLocation.isHigherLevel;
        });
    };

    /**
     * Return child admission locations by parent location uuid from list of admission locations
     *
     * @param admissionLocations
     * @param parentUuid
     * @returns {object}
     */
    getChildAdmissionLocations = (admissionLocations, parentUuid) => {
        if (parentUuid == null) {
            return this.getHigherLevelAdmissionLocations(admissionLocations);
        }

        return pickBy(admissionLocations, (admissionLocation) => {
            return admissionLocation.parentAdmissionLocationUuid == parentUuid;
        });
    };

    /**
     * Return parent admission by admission location uuid from list of admission locations
     *
     * @param admissionLocations
     * @param admissionLocationUuid
     * @returns {object}
     */
    getParentAdmissionLocation = (admissionLocations, admissionLocationUuid) => {
        const parentAdmissionLocationUuid = admissionLocations[admissionLocationUuid].parentAdmissionLocationUuid;
        return typeof admissionLocations[parentAdmissionLocationUuid] != 'undefined'
            ? admissionLocations[parentAdmissionLocationUuid]
            : null;
    };

    /**
     * Navigate and fetch locations upto higher level form given admission location uuid.
     *
     * @param admissionLocations
     * @param navigateLocations
     * @param admissionLocationUuid
     * @returns {array}
     */
    navigateUpToHigherLevel = (admissionLocations, navigateLocations, admissionLocationUuid) => {
        if (admissionLocationUuid != null && typeof admissionLocations[admissionLocationUuid] != 'undefined') {
            const admissionLocation = admissionLocations[admissionLocationUuid];
            navigateLocations.unshift(admissionLocation);
            return this.navigateUpToHigherLevel(
                admissionLocations,
                navigateLocations,
                admissionLocation.parentAdmissionLocationUuid
            );
        } else {
            return navigateLocations;
        }
    };
}
