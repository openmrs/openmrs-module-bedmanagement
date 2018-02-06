import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import {IntlProvider} from 'react-intl';

import AdmissionLocationList from 'components/admissionLocation/rightPanel/admissionLocationList';
import admissionLocationFunctionsMock from 'components/__mocks__/admissionLocationFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        admissionLocationFunctions: admissionLocationFunctionsMock,
        admissionLocations: admissionLocationFunctionsMock.getAdmissionLocations()
    },
    context: {
        intl: intl
    }
};

describe('AdmissionLocationList', () => {
    it('Should render location list properly', () => {
        const generalWardLocationList = shallow(
            <AdmissionLocationList
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                admissionLocationFunctions={testData.props.admissionLocationFunctions}
            />,
            {context: testData.context}
        );

        expect(generalWardLocationList.find('LocationBlock').length).toBe(2);
        expect(shallowToJson(generalWardLocationList)).toMatchSnapshot();

        const labourWardLocationList = shallow(
            <AdmissionLocationList
                activeUuid="bb0e512e-d225-11e4-9c67-080027b662ec"
                admissionLocationFunctions={testData.props.admissionLocationFunctions}
            />,
            {context: testData.context}
        );

        expect(labourWardLocationList.find('LocationBlock').length).toBe(0);
        expect(shallowToJson(labourWardLocationList)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnAddWardClickHandler = jest.spyOn(AdmissionLocationList.prototype, 'addWardClickHandler');
        const spyOnSetState = jest.spyOn(testData.props.admissionLocationFunctions, 'setState');
        const generalWardLocationList = mount(
            <AdmissionLocationList
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                admissionLocationFunctions={testData.props.admissionLocationFunctions}
            />,
            {context: testData.context}
        );

        generalWardLocationList.find('.fa-plus').simulate('click');
        expect(spyOnAddWardClickHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEditLocation',
            pageData: {
                operation: 'add'
            },
            activeUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec'
        });
    });
});
