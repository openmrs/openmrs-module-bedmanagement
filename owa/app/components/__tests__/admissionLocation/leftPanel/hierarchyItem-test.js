import React from 'react';
import { shallowToJson } from 'enzyme-to-json';
import {shallow, mount} from 'enzyme';

import HierarchyItem from 'components/admissionLocation/leftPanel/hierarchyItem';
import hierarchyFunctionMock from 'components/__mocks__/hierarchyFunctions-mock';

const testProp = {
    hierarchyFunction : hierarchyFunctionMock,
    admissionLocations : hierarchyFunctionMock.getAdmissionLocations()
};

describe('HierarchyItem', () => {
    it('Renders correctly General ward hierarchy', () => {
        const hierarchyItem = shallow(
            <HierarchyItem key="baf7bd38-d225-11e4-9c67-080027b662ec"
                isParentOpen={true}
                hierarchyFunction={testProp.hierarchyFunction}
                admissionLocation={testProp.admissionLocations['baf7bd38-d225-11e4-9c67-080027b662ec']}/>
        );

        expect(shallowToJson(hierarchyItem)).toMatchSnapshot();
    });

    it('Renders correctly Labour ward hierarchy', () => {
        const hierarchyItem = shallow(
            <HierarchyItem
                isParentOpen={true}
                hierarchyFunction={testProp.hierarchyFunction}
                admissionLocation={testProp.admissionLocations['bb0e512e-d225-11e4-9c67-080027b662ec']}/>);

        expect(hierarchyItem.find('ul').text().trim()).toBe('Labour Ward');
        expect(shallowToJson(hierarchyItem)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnClickTitle = jest.spyOn(HierarchyItem.prototype, 'onClickTitle');
        const spyOnClickIcon = jest.spyOn(HierarchyItem.prototype, 'onClickIcon');
        const spySetState = jest.spyOn(testProp.hierarchyFunction, 'setState');
        const spySetAdmissionLocationIsOpen = jest.spyOn(testProp.hierarchyFunction, 'setAdmissionLocationIsOpen');
        const spyToggleIsOpen = jest.spyOn(testProp.hierarchyFunction, 'toggleIsOpen');

        const hierarchyItem = mount(
            <HierarchyItem
                isParentOpen={true}
                hierarchyFunction={testProp.hierarchyFunction}
                admissionLocation={testProp.admissionLocations['bb0e512e-d225-11e4-9c67-080027b662ec']}/>);

        hierarchyItem.find('span').simulate('click');
        expect(spyOnClickTitle).toHaveBeenCalled();
        expect(spySetAdmissionLocationIsOpen).toHaveBeenCalledWith('bb0e512e-d225-11e4-9c67-080027b662ec', true);
        expect(spySetState).toHaveBeenCalledWith({
            activeUuid : 'bb0e512e-d225-11e4-9c67-080027b662ec',
            activePage: 'listing',
            pageData: {}
        });

        hierarchyItem.find('i').simulate('click');
        expect(spyOnClickIcon).toHaveBeenCalled();
        expect(spyToggleIsOpen).toHaveBeenCalledWith('bb0e512e-d225-11e4-9c67-080027b662ec', false);
    });
});