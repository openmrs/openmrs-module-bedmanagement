import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import _ from 'lodash';

import Breadcomb from 'components/admissionLocation/rightPanel/breadcrumb';
import admissionLocationFunctionsMock from 'components/__mock__/admissionLocationFunctionsMock';

const testProps = {
    admissionLocationFunctions: admissionLocationFunctionsMock
};

describe('Breadcomb', () => {
    it('Should render properly', () => {
        const breadcomb = shallow(<Breadcomb activeUuid="e48fb2b3-d490-11e5-b193-0800270d80ce"
            admissionLocationFunctions={testProps.admissionLocationFunctions}/>);

        expect(breadcomb.find('ul li').length).toBe(3);
        expect(breadcomb.find('ul li').at(1).find('a').text()).toBe('General Ward');
        expect(shallowToJson(breadcomb)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnClickHandler = jest.spyOn(Breadcomb.prototype, 'clickHandler');
        const spyOnSetActiveLocationUuid = jest.spyOn(testProps.admissionLocationFunctions, 'setActiveLocationUuid');
        const breadcomb = mount(<Breadcomb activeUuid="e48fb2b3-d490-11e5-b193-0800270d80ce"
            admissionLocationFunctions={testProps.admissionLocationFunctions}/>);

        breadcomb.find('ul li').at(1).find('a').simulate('click');
        expect(spyOnClickHandler).toHaveBeenCalled();
        expect(spyOnSetActiveLocationUuid).toHaveBeenCalledWith('baf7bd38-d225-11e4-9c67-080027b662ec');
    });

    it('Should return breadcrumb locations', () => {
        const breadcomb = mount(<Breadcomb activeUuid="e48fb2b3-d490-11e5-b193-0800270d80ce"
            admissionLocationFunctions={testProps.admissionLocationFunctions}/>);

        const expectedBreadcrumbLocations = [
            {
                name: 'General Ward',
                uuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
                description: 'Hospital General Ward Section',
                parentAdmissionLocationUuid: 'c1e42932-3f10-11e4-adec-0800271c1b75',
                isOpen: false,
                isHigherLevel: true
            },
            {
                name: 'General Ward - Room 2',
                uuid: 'e48fb2b3-d490-11e5-b193-0800270d80ce',
                description: 'General Ward room number 2',
                parentAdmissionLocationUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec',
                isOpen: false,
                isHigherLevel: false
            }];

        expect(_.isEqual(breadcomb.instance().breadcrumbLocations(), expectedBreadcrumbLocations)).toEqual(true);
    });
});