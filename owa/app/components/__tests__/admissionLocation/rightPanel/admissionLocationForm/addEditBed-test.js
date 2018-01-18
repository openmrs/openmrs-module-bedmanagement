import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

import AddEditBed from 'components/admissionLocation/rightPanel/admissionLocationForm/addEditBed';
import admissionLocationFunctionsMock from 'components/__mocks__/admissionLocationFunctions-mock';

require('components/__mocks__/location-mock');
const testProps = {
    admissionLocationFunctions: admissionLocationFunctionsMock,
    admissionLocations: admissionLocationFunctionsMock.getAdmissionLocations(),
    addBedData: {
        bedId: null,
        bedNumber: null,
        bedType: null,
        bedUuid: null,
        columnNumber: 2,
        rowNumber: 1,
        status: null,
        layoutColumn: 4,
        layoutRow: 3
    },
    editBedData: {
        bedId: 42,
        bedNumber: '100-a',
        bedType: {
            description: 'LXY',
            displayName: 'luxury bed',
            id: 2,
            name: 'luxury'
        },
        bedUuid: 'a3e42812-a4c1-4453-96cf-d45578d0fea9',
        columnNumber: 1,
        rowNumber: 2,
        status: 'AVAILABLE',
        layoutColumn: 3,
        layoutRow: 4
    }
};

describe('AddEditBed', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            id: 53,
            uuid: 'a3e42812-a4c1-4453-96cf-d45578d0fea9',
            bedNumber: '100-A',
            bedType: {
                id: 1,
                name: 'deluxe',
                displayName: 'deluxe bed',
                description: 'DLX'
            },
            row: 2,
            column: 1,
            status: 'AVAILABLE'
        };

        mock
            .onPost('https://192.168.33.10/openmrs/ws/rest/v1/bed/a3e42812-a4c1-4453-96cf-d45578d0fea9')
            .reply(200, data);
    });

    it('Should display add edit bed form properly', () => {
        const addBedForm = shallow(
            <AddEditBed
                operation="add"
                layoutColumn={4}
                layoutRow={3}
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                bed={testProps.addBedData}
                bedTypes={testProps.admissionLocationFunctions.getBedTypes()}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );

        expect(
            addBedForm
                .find('#location-name')
                .text()
                .trim()
        ).toBe('General Ward');
        expect(addBedForm.find('#row-field').props().value).toBe(1);
        expect(addBedForm.find('#column-field').props().value).toBe(2);
        expect(addBedForm.find('#bed-number-field').props().value).toBe('');
        expect(shallowToJson(addBedForm)).toMatchSnapshot();

        const editBedForm = shallow(
            <AddEditBed
                operation="edit"
                layoutColumn={4}
                layoutRow={3}
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                bed={testProps.editBedData}
                bedTypes={testProps.admissionLocationFunctions.getBedTypes()}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );

        expect(
            editBedForm
                .find('#location-name')
                .text()
                .trim()
        ).toBe('General Ward');
        expect(editBedForm.find('#row-field').props().value).toBe(2);
        expect(editBedForm.find('#column-field').props().value).toBe(1);
        expect(editBedForm.find('#bed-number-field').props().value).toBe('100-a');
        expect(editBedForm.find('#bed-type').props().value).toBe('luxury');
        expect(shallowToJson(editBedForm)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnCancelEventHandler = jest.spyOn(AddEditBed.prototype, 'cancelEventHandler');
        const spyOnSubmitHandler = jest.spyOn(AddEditBed.prototype, 'onSubmitHandler');
        const spyOnChangeRowField = jest.spyOn(AddEditBed.prototype, 'onChangeRowField');
        const spyOnChangeColumnField = jest.spyOn(AddEditBed.prototype, 'onChangeColumnField');
        const spnOnChangeBedNumberField = jest.spyOn(AddEditBed.prototype, 'onChangeBedNumberField');
        const spyOnChangeBedType = jest.spyOn(AddEditBed.prototype, 'onChangeBedType');
        const spySetState = jest.spyOn(testProps.admissionLocationFunctions, 'setState');
        const editBedForm = mount(
            <AddEditBed
                operation="edit"
                layoutColumn={4}
                layoutRow={3}
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                bed={testProps.editBedData}
                bedTypes={testProps.admissionLocationFunctions.getBedTypes()}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );

        editBedForm.find('#row-field').simulate('change');
        expect(spyOnChangeRowField).toHaveBeenCalled();
        editBedForm.find('#column-field').simulate('change');
        expect(spyOnChangeColumnField).toHaveBeenCalled();
        editBedForm.find('#bed-number-field').simulate('change');
        expect(spnOnChangeBedNumberField).toHaveBeenCalled();
        editBedForm.find('#bed-type').simulate('change');
        expect(spyOnChangeBedType).toHaveBeenCalled();

        editBedForm.find("input[name='cancel']").simulate('click');
        expect(spyOnCancelEventHandler).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activePage: 'listing',
            pageData: {},
            activeUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec'
        });

        editBedForm.find("input[type='submit']").simulate('submit');
        expect(spyOnSubmitHandler).toHaveBeenCalled();
    });
});
