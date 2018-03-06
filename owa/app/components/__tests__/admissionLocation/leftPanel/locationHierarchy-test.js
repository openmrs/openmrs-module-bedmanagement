import React from 'react';
import {shallowToJson} from 'enzyme-to-json';
import {shallow, mount} from 'enzyme';
import {IntlProvider} from 'react-intl';

import LocationHierarchy from 'components/admissionLocation/leftPanel/locationHierarchy';
import admissionLocationFunctionsMock from 'components/__mocks__/admissionLocationFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        admissionLocationFunctions: admissionLocationFunctionsMock
    },
    context: {
        intl: intl
    }
};

describe('LocationHierarchy', () => {
    it('Renders correctly Location hierarchy', () => {
        const locationHierarchy = shallow(
            <LocationHierarchy isOpen={true} admissionLocationFunctions={testData.props.admissionLocationFunctions} />,
            {context: testData.context}
        );

        expect(shallowToJson(locationHierarchy)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnClickTitle = jest.spyOn(LocationHierarchy.prototype, 'onClickTitle');
        const spyOnClickIcon = jest.spyOn(LocationHierarchy.prototype, 'onClickIcon');
        const spySetState = jest.spyOn(testData.props.admissionLocationFunctions, 'setState');
        const locationHierarchy = mount(
            <LocationHierarchy isOpen={true} admissionLocationFunctions={testData.props.admissionLocationFunctions} />,
            {context: testData.context}
        );

        locationHierarchy.find('li.title span').simulate('click');
        expect(spyOnClickTitle).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activeUuid: null,
            isOpen: true,
            activePage: 'listing',
            pageData: {}
        });

        locationHierarchy.find('li.title i').simulate('click');
        expect(spyOnClickIcon).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({isOpen: false});
    });

    it('Should function run properly', () => {
        const locationHierarchy = mount(
            <LocationHierarchy isOpen={true} admissionLocationFunctions={testData.props.admissionLocationFunctions} />,
            {context: testData.context}
        );
        const hierarchyFunction = locationHierarchy.instance().hierarchyFunction;
        const spySetActiveLocationUuid = jest.spyOn(testData.props.admissionLocationFunctions, 'setActiveLocationUuid');
        const spySetState = jest.spyOn(testData.props.admissionLocationFunctions, 'setState');
        const spySetAdmissionLocationIsOpen = jest.spyOn(hierarchyFunction, 'setAdmissionLocationIsOpen');

        expect(hierarchyFunction.getActiveUuid()).toBe('baf7bd38-d225-11e4-9c67-080027b662ec');
        expect(hierarchyFunction.getAdmissionLocations()).toBe(
            testData.props.admissionLocationFunctions.getAdmissionLocations()
        );

        hierarchyFunction.setActiveUuid('bb0e512e-d225-11e4-9c67-080027b662ec');
        expect(spySetActiveLocationUuid).toHaveBeenCalledWith('bb0e512e-d225-11e4-9c67-080027b662ec');

        hierarchyFunction.toggleIsOpen('baf7bd38-d225-11e4-9c67-080027b662ec', true);
        expect(spySetAdmissionLocationIsOpen).toHaveBeenCalledWith('baf7bd38-d225-11e4-9c67-080027b662ec', false);

        hierarchyFunction.toggleIsOpen(null, true);
        expect(spySetState).toHaveBeenCalledWith({isOpen: false});

        hierarchyFunction.setAdmissionLocationIsOpen('baf7bd38-d225-11e4-9c67-080027b662ec', true);
        const admissionLocations = hierarchyFunction.getAdmissionLocations();
        expect(admissionLocations['baf7bd38-d225-11e4-9c67-080027b662ec'].isOpen).toBe(true);
    });
});
