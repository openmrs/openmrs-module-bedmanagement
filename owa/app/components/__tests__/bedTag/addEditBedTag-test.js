import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

import AddEditBedTag from 'components/bedTag/bedTagForm/addEditBedTag';
import bedTagFunctionsMock from 'components/__mocks__/bedTagFunctions-mock';

const testProps = {
    bedTagFunctions: bedTagFunctionsMock
};

require('components/__mocks__/location-mock');
describe('AddEditBedTag', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            'id':4,
            'name':'Isolation',
            'uuid':'ff7ed494-7b9c-4478-812a-5187e297f94c'
        };

        mock.onPost('https://192.168.33.10/openmrs/ws/rest/v1/bedTag/ff7ed494-7b9c-4478-812a-5187e297f94c')
            .reply(200, data);
    });

    it('Should display add edit bed tag form properly', () => {
        const addBedTagForm = shallow(<AddEditBedTag bedTagFunctions={testProps.bedTagFunctions} bedTagUuid={null} operation='add'/>);
        expect(addBedTagForm.find('#name-field').props().value).toBe('');
        expect(shallowToJson(addBedTagForm)).toMatchSnapshot();


        const editBedTagForm = shallow(<AddEditBedTag bedTagFunctions={testProps.bedTagFunctions} operation='edit'
            bedTagUuid='ff7ed494-7b9c-4478-812a-5187e297f94c' />);

        expect(editBedTagForm.find('#name-field').props().value).toBe('Isolation');
        expect(shallowToJson(editBedTagForm)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnCancelEventHandler = jest.spyOn(AddEditBedTag.prototype, 'cancelEventHandler');
        const spyOnSubmitHandler = jest.spyOn(AddEditBedTag.prototype, 'onSubmitHandler');
        const spyOnChangeNameField = jest.spyOn(AddEditBedTag.prototype, 'onChangeNameField');
        const spySetState = jest.spyOn(testProps.bedTagFunctions, 'setState');
        const editBedForm = mount(<AddEditBedTag bedTagFunctions={testProps.bedTagFunctions} operation='edit'
            bedTagUuid='ff7ed494-7b9c-4478-812a-5187e297f94c' />);

        editBedForm.find('#name-field').simulate('change');
        expect(spyOnChangeNameField).toHaveBeenCalled();

        editBedForm.find('.btn-danger').simulate('click');
        expect(spyOnCancelEventHandler).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activePage: 'listing',
            pageData: {}
        });

        editBedForm.find('input[type=\'submit\']').simulate('submit');
        expect(spyOnSubmitHandler).toHaveBeenCalled();
    });
});