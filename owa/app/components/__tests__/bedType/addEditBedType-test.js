import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {IntlProvider} from 'react-intl';

import AddEditBedType from 'components/bedType/bedTypeForm/addEditBedType';
import bedTypeFunctionsMock from 'components/__mocks__/bedTypeFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        bedTypeFunctions: bedTypeFunctionsMock
    },
    context: {
        intl: intl
    }
};

require('components/__mocks__/location-mock');
describe('AddEditBedType', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            uuid: '6f9fb240-0fd5-11e8-adb7-080027b38971',
            name: 'luxury',
            displayName: 'luxury bed',
            description: 'LXY'
        };

        mock.onPost('https://192.168.33.10/openmrs/ws/rest/v1/bedtype/2').reply(200, data);
    });

    it('Should display add edit bed type form properly', () => {
        const addBedTypeForm = shallow(
            <AddEditBedType bedTypeFunctions={testData.props.bedTypeFunctions} bedTypeUuid={null} operation="add" />,
            {context: testData.context}
        );
        expect(addBedTypeForm.find('#name-field').props().value).toBe('');
        expect(shallowToJson(addBedTypeForm)).toMatchSnapshot();

        const editBedTypeForm = shallow(
            <AddEditBedType
                bedTypeFunctions={testData.props.bedTypeFunctions}
                operation="edit"
                bedTypeUuid="6f9fb240-0fd5-11e8-adb7-080027b38971"
            />,
            {context: testData.context}
        );

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
        const spySetState = jest.spyOn(testData.props.bedTypeFunctions, 'setState');
        const editBedTypeForm = mount(
            <AddEditBedType
                bedTypeFunctions={testData.props.bedTypeFunctions}
                operation="edit"
                bedTypeUuid="6f9fb240-0fd5-11e8-adb7-080027b38971"
            />,
            {context: testData.context}
        );

        editBedTypeForm.find('#name-field').simulate('change');
        expect(spyOnChangeNameField).toHaveBeenCalled();
        editBedTypeForm.find('#display-name-field').simulate('change');
        expect(spyOnChangeDisplayNameField).toHaveBeenCalled();
        editBedTypeForm.find('#description-field').simulate('change');
        expect(spyOnChangeDescription).toHaveBeenCalled();

        editBedTypeForm.find("input[name='cancel']").simulate('click');
        expect(spyOnCancelEventHandler).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activePage: 'listing',
            pageData: {}
        });

        editBedTypeForm.find("input[type='submit']").simulate('submit');
        expect(spyOnSubmitHandler).toHaveBeenCalled();
    });
});
