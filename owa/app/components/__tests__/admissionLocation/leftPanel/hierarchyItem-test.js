import React from 'react';
import {shallowToJson} from 'enzyme-to-json';
import {shallow, mount} from 'enzyme';
import {IntlProvider} from 'react-intl';

import HierarchyItem from 'components/admissionLocation/leftPanel/hierarchyItem';
import hierarchyFunctionMock from 'components/__mocks__/hierarchyFunctions-mock';
import messages from 'i18n/messages';

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        hierarchyFunction: hierarchyFunctionMock,
        admissionLocations: hierarchyFunctionMock.getAdmissionLocations()
    },
    context: {
        intl: intl
    }
};

describe('HierarchyItem', () => {
    it('Renders correctly General ward hierarchy', () => {
        const hierarchyItem = shallow(
            <HierarchyItem
                key="baf7bd38-d225-11e4-9c67-080027b662ec"
                isParentOpen={true}
                hierarchyFunction={testData.props.hierarchyFunction}
                admissionLocation={testData.props.admissionLocations['baf7bd38-d225-11e4-9c67-080027b662ec']}
            />,
            {context: testData.context}
        );

        expect(shallowToJson(hierarchyItem)).toMatchSnapshot();
    });

    it('Renders correctly Labour ward hierarchy', () => {
        const hierarchyItem = shallow(
            <HierarchyItem
                isParentOpen={true}
                hierarchyFunction={testData.props.hierarchyFunction}
                admissionLocation={testData.props.admissionLocations['bb0e512e-d225-11e4-9c67-080027b662ec']}
            />,
            {context: testData.context}
        );

        expect(
            hierarchyItem
                .find('ul')
                .text()
                .trim()
        ).toBe('Labour Ward');
        expect(shallowToJson(hierarchyItem)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnClickTitle = jest.spyOn(HierarchyItem.prototype, 'onClickTitle');
        const spyOnClickIcon = jest.spyOn(HierarchyItem.prototype, 'onClickIcon');
        const spySetState = jest.spyOn(testData.props.hierarchyFunction, 'setState');
        const spySetAdmissionLocationIsOpen = jest.spyOn(
            testData.props.hierarchyFunction,
            'setAdmissionLocationIsOpen'
        );
        const spyToggleIsOpen = jest.spyOn(testData.props.hierarchyFunction, 'toggleIsOpen');

        const hierarchyItem = mount(
            <HierarchyItem
                isParentOpen={true}
                hierarchyFunction={testData.props.hierarchyFunction}
                admissionLocation={testData.props.admissionLocations['bb0e512e-d225-11e4-9c67-080027b662ec']}
            />,
            {context: testData.context}
        );

        hierarchyItem.find('span').simulate('click');
        expect(spyOnClickTitle).toHaveBeenCalled();
        expect(spySetAdmissionLocationIsOpen).toHaveBeenCalledWith('bb0e512e-d225-11e4-9c67-080027b662ec', true);
        expect(spySetState).toHaveBeenCalledWith({
            activeUuid: 'bb0e512e-d225-11e4-9c67-080027b662ec',
            activePage: 'listing',
            pageData: {}
        });

        hierarchyItem.find('i').simulate('click');
        expect(spyOnClickIcon).toHaveBeenCalled();
        expect(spyToggleIsOpen).toHaveBeenCalledWith('bb0e512e-d225-11e4-9c67-080027b662ec', false);
    });
});
