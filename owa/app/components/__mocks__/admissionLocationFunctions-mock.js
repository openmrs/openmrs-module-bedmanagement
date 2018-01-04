import pickBy from 'lodash.pickby';
import {admissionLocations, visitLocations, bedTypes} from 'utilities/__tests__/testData.json';

const admissionLocationFunctionsMock = {
    setActiveLocationUuid: (admissionLocationUuid) => jest.fn(),
    getActiveLocationUuid: () => {
        return 'baf7bd38-d225-11e4-9c67-080027b662ec';
    },
    setState: (data) => jest.fn(),
    getState: () => jest.fn(),
    getAdmissionLocations: () => {
        return admissionLocations;
    },
    getVisitLocations: () => {
        return visitLocations;
    },
    getBedTypes: () => {
        return bedTypes;
    },
    getAdmissionLocationByUuid: (admissionLocationUuid) => {
        return typeof admissionLocations[admissionLocationUuid] !== 'undefined' ?
            admissionLocations[admissionLocationUuid] : null;
    },
    getParentAdmissionLocation: (admissionLocationUuid) => {
        const parentAdmissionLocationUuid = admissionLocations[admissionLocationUuid].parentAdmissionLocationUuid;
        return parentAdmissionLocationUuid != null ? admissionLocations[parentAdmissionLocationUuid] : null;
    },
    getChildAdmissionLocations : (admissionLocationUuid) => {
        return  pickBy(admissionLocations, (admissionLocation) => {
            return admissionLocation.parentAdmissionLocationUuid == admissionLocationUuid;
        });
    },
    reFetchAllAdmissionLocations : () => jest.fn(),
    notify : (notifyType, message) => jest.fn()
};

export default admissionLocationFunctionsMock;
