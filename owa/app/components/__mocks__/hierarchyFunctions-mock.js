import {admissionLocations} from 'utilities/__tests__/testData.json';

const hierarchyFunctionMock = {
    getActiveUuid: () => {
        return 'baf7bd38-d225-11e4-9c67-080027b662ec';
    },
    getAdmissionLocations: () => {
        return admissionLocations;
    },
    setActiveUuid: (locationUuid) => jest.fn(),
    setState: (stateData) => jest.fn(),
    toggleIsOpen: (locationUuid, isOpen) => jest.fn(),
    setAdmissionLocationIsOpen: (locationUuid, isOpen) => jest.fn()
};

export default hierarchyFunctionMock;