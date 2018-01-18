import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

import AddEditAdmissionLocation from 'components/admissionLocation/rightPanel/admissionLocationForm/addEditAdmissionLocation';
import admissionLocationFunctionsMock from 'components/__mocks__/admissionLocationFunctions-mock';

require('components/__mocks__/location-mock');
const testProps = {
    admissionLocationFunctions: admissionLocationFunctionsMock,
    admissionLocations: admissionLocationFunctionsMock.getAdmissionLocations()
};

describe('AddEditAdmissionLocation', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            ward: {
                uuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
                display: 'General Ward',
                name: 'General Ward',
                description: 'Hospital General Ward Section'
            }
        };

        mock
            .onPost('https://192.168.33.10/openmrs/ws/rest/v1/admissionLocation/baf7bd38-d225-11e4-9c67-080027b662ec')
            .reply(200, data);
    });

    it('Should display admission location form properly', () => {
        const addAdmissionLocationForm = shallow(
            <AddEditAdmissionLocation
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                admissionLocationFunctions={testProps.admissionLocationFunctions}
                operation="add"
            />
        );
        expect(
            addAdmissionLocationForm
                .find('span')
                .text()
                .trim()
        ).toBe('General Ward');
        expect(addAdmissionLocationForm.find("input[type='text']").props().value).toBe('');
        expect(addAdmissionLocationForm.find('textarea').props().value).toBe('');
        expect(shallowToJson(addAdmissionLocationForm)).toMatchSnapshot();

        const editAdmissionLocationForm = shallow(
            <AddEditAdmissionLocation
                activeUuid="e48fb2b3-d490-11e5-b193-0800270d80ce"
                admissionLocationFunctions={testProps.admissionLocationFunctions}
                operation="edit"
            />
        );
        expect(
            editAdmissionLocationForm
                .find('span')
                .text()
                .trim()
        ).toBe('General Ward');
        expect(editAdmissionLocationForm.find("input[type='text']").props().value).toBe('General Ward - Room 2');
        expect(editAdmissionLocationForm.find('textarea').props().value).toBe('General Ward room number 2');
        expect(shallowToJson(editAdmissionLocationForm)).toMatchSnapshot();

        const editTopLevelAdmissionLocationForm = shallow(
            <AddEditAdmissionLocation
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                admissionLocationFunctions={testProps.admissionLocationFunctions}
                operation="edit"
            />
        );
        expect(editTopLevelAdmissionLocationForm.find('select').props().value).toBe(
            'c1e42932-3f10-11e4-adec-0800271c1b75'
        );
        expect(editTopLevelAdmissionLocationForm.find("input[type='text']").props().value).toBe('General Ward');
        expect(editTopLevelAdmissionLocationForm.find('textarea').props().value).toBe('Hospital General Ward Section');
        expect(shallowToJson(editTopLevelAdmissionLocationForm)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnCancelEventHandler = jest.spyOn(AddEditAdmissionLocation.prototype, 'cancelEventHandler');
        const spyOnSubmitHandler = jest.spyOn(AddEditAdmissionLocation.prototype, 'onSubmitHandler');
        const spyOnSelectParentLocation = jest.spyOn(AddEditAdmissionLocation.prototype, 'onSelectParentLocation');
        const spyOnChangeDescriptionField = jest.spyOn(AddEditAdmissionLocation.prototype, 'onChangeDescriptionField');
        const spyOnChangeNameField = jest.spyOn(AddEditAdmissionLocation.prototype, 'onChangeNameField');
        const spySetState = jest.spyOn(testProps.admissionLocationFunctions, 'setState');
        const editTopLevelAdmissionLocationForm = mount(
            <AddEditAdmissionLocation
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                admissionLocationFunctions={testProps.admissionLocationFunctions}
                operation="edit"
            />
        );

        editTopLevelAdmissionLocationForm.find("input[type='text']").simulate('change');
        expect(spyOnChangeNameField).toHaveBeenCalled();

        editTopLevelAdmissionLocationForm.find('textarea').simulate('change');
        expect(spyOnChangeDescriptionField).toHaveBeenCalled();

        editTopLevelAdmissionLocationForm.find('select').simulate('change');
        expect(spyOnSelectParentLocation).toHaveBeenCalled();

        editTopLevelAdmissionLocationForm.find("input[name='cancel']").simulate('click');
        expect(spyOnCancelEventHandler).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activePage: 'listing',
            pageData: {},
            activeUuid: null
        });

        editTopLevelAdmissionLocationForm.find("input[type='submit']").simulate('submit');
        expect(spyOnSubmitHandler).toHaveBeenCalled();
    });
});
