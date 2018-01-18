import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

import LocationBlock from 'components/admissionLocation/rightPanel/locationBlock';
import admissionLocationFunctionsMock from 'components/__mocks__/admissionLocationFunctions-mock';

require('components/__mocks__/location-mock');
const testProps = {
    admissionLocationFunctions: admissionLocationFunctionsMock,
    admissionLocations: admissionLocationFunctionsMock.getAdmissionLocations()
};

describe('LocationBlock', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {};

        mock
            .onDelete('https://192.168.33.10/openmrs/ws/rest/v1/admissionLocation/baf7bd38-d225-11e4-9c67-080027b662ec')
            .reply(200, data);
    });

    it('Should display location block properly', () => {
        const generalWardLocationBlock = shallow(
            <LocationBlock
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                admissionLocation={testProps.admissionLocations['baf7bd38-d225-11e4-9c67-080027b662ec']}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );

        expect(
            generalWardLocationBlock
                .find('span')
                .text()
                .trim()
        ).toBe('General Ward');
        expect(shallowToJson(generalWardLocationBlock)).toMatchSnapshot();

        const labourWardLocationBlock = shallow(
            <LocationBlock
                activeUuid="bb0e512e-d225-11e4-9c67-080027b662ec"
                admissionLocation={testProps.admissionLocations['bb0e512e-d225-11e4-9c67-080027b662ec']}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );

        expect(
            labourWardLocationBlock
                .find('span')
                .text()
                .trim()
        ).toBe('Labour Ward');
        expect(shallowToJson(labourWardLocationBlock)).toMatchSnapshot();
    });

    it('Should trigger event handler', () => {
        const spyOnClickHandler = jest.spyOn(LocationBlock.prototype, 'onClickHandler');
        const spyOnEditWardClickHandler = jest.spyOn(LocationBlock.prototype, 'editWardClickHandler');
        const spyOnDeleteHandler = jest.spyOn(LocationBlock.prototype, 'onDeleteHandler');
        const spySetState = jest.spyOn(testProps.admissionLocationFunctions, 'setState');
        const generalWardLocationBlock = mount(
            <LocationBlock
                activeUuid="baf7bd38-d225-11e4-9c67-080027b662ec"
                admissionLocation={testProps.admissionLocations['baf7bd38-d225-11e4-9c67-080027b662ec']}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );

        generalWardLocationBlock.find('.location.block').simulate('click');
        expect(spyOnClickHandler).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activePage: 'listing',
            pageData: {},
            activeUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec'
        });

        generalWardLocationBlock.find('.fa-pencil').simulate('click');
        expect(spyOnEditWardClickHandler).toHaveBeenCalled();
        expect(spySetState).toHaveBeenCalledWith({
            activePage: 'addEditLocation',
            pageData: {
                operation: 'edit'
            },
            activeUuid: 'baf7bd38-d225-11e4-9c67-080027b662ec'
        });

        const labourWardLocationBlock = mount(
            <LocationBlock
                activeUuid="bb0e512e-d225-11e4-9c67-080027b662ec"
                admissionLocation={testProps.admissionLocations['bb0e512e-d225-11e4-9c67-080027b662ec']}
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );
        labourWardLocationBlock.find('.fa-trash').simulate('click');
        expect(spyOnDeleteHandler).toHaveBeenCalled();
    });
});
