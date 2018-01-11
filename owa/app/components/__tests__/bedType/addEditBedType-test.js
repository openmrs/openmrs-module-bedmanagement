import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

import AddEditBedType from 'components/bedType/bedTypeForm/addEditBedType';
import bedTypeFunctionsMock from 'components/__mocks__/bedTypeFunctions-mock';

const testProps = {
    bedTypeFunctions: bedTypeFunctionsMock
};

require('components/__mocks__/location-mock');
describe('AddEditBedType', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            'id':2,
            'name':'luxury',
            'displayName': 'luxury bed',
            'description': 'LXY'
        };

        mock.onPost('https://192.168.33.10/openmrs/ws/rest/v1/bedtype/2')
            .reply(200, data);
    });

    it('Should display add edit bed type form properly', () => {
        const addBedTypeForm = shallow(<AddEditBedType bedTypeFunctions={testProps.bedTypeFunctions} bedTypeId={null} operation='add'/>);
        expect(addBedTypeForm.find('#name-field').props().value).toBe('');
        expect(shallowToJson(addBedTypeForm)).toMatchSnapshot();


        const editBedTypeForm = shallow(<AddEditBedType bedTypeFunctions={testProps.bedTypeFunctions} operation='edit'
            bedTypeId={2} />);

        expect(editBedTypeForm.find('#name-field').props().value).toBe('luxury');
        expect(editBedTypeForm.find('#display-name-field').props().value).toBe('luxury bed');
        expect(editBedTypeForm.find('#description-field').props().value).toBe('LXY');
        expect(shallowToJson(editBedTypeForm)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnCancelEventHandler = jest.spyOn(AddEditBedType.prototype, 'cancelEventHandler');
        const spyOnSubmitHandler = jest.spyOn(AddEditBedType.prototype, 'onSubmitHandler');
        const spyOnChangeNameField = jest.spyOn(AddEditBedType.prototype, 'onChangeNameField');
        const spyOnChangeDisplayNameField = jest.spyOn(AddEditBedType.prototype, 'onChangeDisplayNameField');
        const spyOnChangeDescription = jest.spyOn(AddEditBedType.prototype, 'onChangeDescription');
        const spySetState = jest.spyOn(testProps.bedTypeFunctions, 'setState');
        const editBedTypeForm = mount(<AddEditBedType bedTypeFunctions={testProps.bedTypeFunctions} operation='edit' bedTypeId={2}/>);

        editBedTypeForm.find('#name-field').simulate('change');
        expect(spyOnChangeNameField).toHaveBeenCalled();
        editBedTypeForm.find('#display-name-field').simulate('change');
        expect(spyOnChangeDisplayNameField).toHaveBeenCalled();
        editBedTypeForm.find('#description-field').simulate('change');
        expect(spyOnChangeDescription).toHaveBeenCalled();

        editBedTypeForm.find('input[name=\'cancel\']').simulate('click');
        expect(spyOnCancelEventHandler).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activePage: 'listing',
            pageData: {}
        });

        editBedTypeForm.find('input[type=\'submit\']').simulate('submit');
        expect(spyOnSubmitHandler).toHaveBeenCalled();
    });
});