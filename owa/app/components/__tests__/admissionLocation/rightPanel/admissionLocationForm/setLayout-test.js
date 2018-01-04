import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

import admissionLocationFunctionsMock from 'components/__mocks__/admissionLocationFunctions-mock';
import SetBedLayout from '../../../../admissionLocation/rightPanel/admissionLocationForm/setBedLayout';

require('components/__mocks__/location-mock');
const testProps = {
    admissionLocationFunctions: admissionLocationFunctionsMock
};

describe('SetBedLayout', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            'ward': {
                'uuid': 'e48fb2b3-d490-11e5-b193-0800270d80ce',
                'display': 'General Ward - Room 2',
                'name': 'New',
                'description': 'General Ward room number 2',
                'tags': [{
                    'uuid': 'a675e840-d225-11e4-9c67-080027b662ec',
                    'display': 'Admission Location',
                    'links': [{
                        'rel': 'self',
                        'uri': 'http://192.168.33.10/openmrs/ws/rest/v1/locationtag/a675e840-d225-11e4-9c67-080027b662ec'
                    }]
                }],
                'parentLocation': {
                    'uuid': 'baf7bd38-d225-11e4-9c67-080027b662ec',
                    'display': 'General Ward',
                    'links': [{
                        'rel': 'self',
                        'uri': 'http://192.168.33.10/openmrs/ws/rest/v1/location/baf7bd38-d225-11e4-9c67-080027b662ec'
                    }]
                },
                'childLocations': [],
                'retired': false,
            },
            'bedLocationMappings': [{
                'rowNumber': 1,
                'columnNumber': 1,
                'bedNumber': null,
                'bedId': null,
                'bedUuid': null,
                'status': null,
                'bedType': null
            }, {
                'rowNumber': 1,
                'columnNumber': 2,
                'bedNumber': null,
                'bedId': null,
                'bedUuid': null,
                'status': null,
                'bedType': null
            }, {
                'rowNumber': 1,
                'columnNumber': 3,
                'bedNumber': null,
                'bedId': null,
                'bedUuid': null,
                'status': null,
                'bedType': null
            }]
        };

        mock.onPost('https://192.168.33.10/openmrs/ws/rest/v1/admissionLocation/baf7bd38-d225-11e4-9c67-080027b662ec')
            .reply(200, data);
    });

    it('Should display set layout form properly', () => {
        const setLayoutForm = shallow(<SetBedLayout activeUuid='baf7bd38-d225-11e4-9c67-080027b662ec'
            admissionLocationFunctions={testProps.admissionLocationFunctions}/>);

        expect(shallowToJson(setLayoutForm)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnCancelEventHandler = jest.spyOn(SetBedLayout.prototype, 'cancelEventHandler');
        const spyOnSubmitHandler = jest.spyOn(SetBedLayout.prototype, 'onSubmitHandler');
        const spyOnChangeRowField = jest.spyOn(SetBedLayout.prototype, 'onChangeRowField');
        const spyOnChangeColumnField = jest.spyOn(SetBedLayout.prototype, 'onChangeColumnField');

        const spySetState = jest.spyOn(testProps.admissionLocationFunctions, 'setState');
        const setLayoutForm = mount(<SetBedLayout activeUuid='baf7bd38-d225-11e4-9c67-080027b662ec'
            admissionLocationFunctions={testProps.admissionLocationFunctions}/>);

        setLayoutForm.find('#row-field').simulate('change');
        expect(spyOnChangeRowField).toHaveBeenCalled();
        setLayoutForm.find('#column-field').simulate('change');
        expect(spyOnChangeColumnField).toHaveBeenCalled();

        setLayoutForm.find('.btn-danger').simulate('click');
        expect(spyOnCancelEventHandler).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activePage: 'listing',
            pageData: {},
            activeUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec'
        });

        setLayoutForm.find('input[type=\'submit\']').simulate('submit');
        expect(spyOnSubmitHandler).toHaveBeenCalled();
    });
});