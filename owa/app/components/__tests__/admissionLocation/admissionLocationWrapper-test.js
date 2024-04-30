import React from 'react';
import {shallow} from "enzyme";
import AdmissionLocationWrapper from "components/admissionLocation/admissionLocationWrapper.js";
import MockAdapter from "axios-mock-adapter";
import axios from "axios/index";
import {IntlProvider} from "react-intl";
import messages from 'i18n/messages';
import {openmrsAPIResponse} from "components/__mocks__/openmrsApiResponse";

require('components/__mocks__/location-mock');

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        match: {
            isExact: true,
            params: {},
            path: '/owa/bedmanagement/admissionLocations.html',
            url: '/owa/bedmanagement/admissionLocations.html'
        }
    },
    context: {
        intl: intl
    },
    sleep: (milisec) => {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                return resolve(true);
            }, milisec);
        });
    }
};


describe('AdmissionLocationWrapper', () => {
    beforeAll(() => {
        const mock = new MockAdapter(axios);
        mock.onGet('https://192.168.33.10/openmrs/ws/rest/v1/location?tag=Visit%20Location&v=full')
            .reply(200, openmrsAPIResponse.visitLocations);

        mock.onGet('https://192.168.33.10/openmrs/ws/rest/v1/location?tag=Admission%20Location&v=full')
            .reply(200, openmrsAPIResponse.admissionLocations);

        mock.onGet('https://192.168.33.10/openmrs/ws/rest/v1/bedtype').reply(200, {results: []});

    });

    it('should fetch Admission and Visit locations on load.', async () => {
        const expectedVisitLocations = {
            "c1e42932-3f10-11e4-adec-0800271c1b75": {
                "uuid": "c1e42932-3f10-11e4-adec-0800271c1b75",
                "name": "Ganiyari",
                "description": "Ganiyari hospital"
            },
            "baf7bd38-d225-11e4-9c67-080027b662ec": {
                "uuid": "baf7bd38-d225-11e4-9c67-080027b662ec",
                "name": "General Ward",
                "description": "General Ward"
            },
            "bb0e512e-d225-11e4-9c67-080027b662ec": {
                "uuid": "bb0e512e-d225-11e4-9c67-080027b662ec",
                "name": "Labour Ward",
                "description": "General Ward"
            },
            "c58e12ed-3f12-11e4-adec-0800271c1b75": {
                "uuid": "c58e12ed-3f12-11e4-adec-0800271c1b75",
                "name": "OPD-1",
                "description": null
            },
            "c5854fd7-3f12-11e4-adec-0800271c1b75": {
                "uuid": "c5854fd7-3f12-11e4-adec-0800271c1b75",
                "name": "Registration Desk",
                "description": null
            },
            "c1f25be5-3f10-11e4-adec-0800271c1b75": {
                "uuid": "c1f25be5-3f10-11e4-adec-0800271c1b75",
                "name": "Subcenter 1 (BAM)",
                "description": "Subcenter 1 (BAM)"
            },
            "c1e4950f-3f10-11e4-adec-0800271c1b75": {
                "uuid": "c1e4950f-3f10-11e4-adec-0800271c1b75",
                "name": "Subcenter 2 (SEM)",
                "description": "Subcenter 2 (SEM)"
            }
        };
        const expectedAdmissionLocations = {
            "baf7bd38-d225-11e4-9c67-080027b662ec": {
                "uuid": "baf7bd38-d225-11e4-9c67-080027b662ec",
                "name": "General Ward",
                "description": "General Ward",
                "parentAdmissionLocationUuid": null,
                "isOpen": false,
                "isHigherLevel": true
            },
            "baf83667-d225-11e4-9c67-080027b662ec": {
                "uuid": "baf83667-d225-11e4-9c67-080027b662ec",
                "name": "General Ward Room 1",
                "description": "1st Floor of General Ward",
                "parentAdmissionLocationUuid": "baf7bd38-d225-11e4-9c67-080027b662ec",
                "isOpen": false,
                "isHigherLevel": false
            },
            "bb0e512e-d225-11e4-9c67-080027b662ec": {
                "uuid": "bb0e512e-d225-11e4-9c67-080027b662ec",
                "name": "Labour Ward",
                "description": "General Ward",
                "parentAdmissionLocationUuid": null,
                "isOpen": false,
                "isHigherLevel": true
            },
            "bb0eb071-d225-11e4-9c67-080027b662ec": {
                "uuid": "bb0eb071-d225-11e4-9c67-080027b662ec",
                "name": "Labour Ward - 1",
                "description": "1st Floor of General Ward",
                "parentAdmissionLocationUuid": "bb0e512e-d225-11e4-9c67-080027b662ec",
                "isOpen": false,
                "isHigherLevel": false
            }
        };

        let admissionLocationWrapper = shallow(<AdmissionLocationWrapper
                match={{path: "openmrs/owa/bedmanagement/admissionLocations.html"}}/>,
            {context: testData.context});

        const admissionLocationFunctions = admissionLocationWrapper.instance().admissionLocationFunctions;

        expect(admissionLocationFunctions.getAdmissionLocations()).toEqual({});
        expect(admissionLocationFunctions.getVisitLocations()).toEqual({});
        await testData.sleep(100);

        expect(admissionLocationFunctions.getAdmissionLocations()).toEqual(expectedAdmissionLocations);
        expect(admissionLocationFunctions.getVisitLocations()).toEqual(expectedVisitLocations);
    })
});
