import React from 'react';
import {shallow} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

import BedTypeWrapper from 'components/bedType/bedTypeWrapper';
require('components/__mocks__/location-mock');
require('babel-polyfill');
const testProps = {
    match: {
        isExact: true,
        params: {},
        path: '/owa/bedmanagement/bedTypes.html',
        url: '/owa/bedmanagement/bedTypes.html'
    },
    sleep: (milisec) => {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                return resolve(true);
            }, milisec);
        });
    }
};

describe('BedTypeWrapper', () => {
    beforeAll(() => {
        //jasmine.DEFAULT_TIMEOUT_INTERVAL = 10000;

        var mock = new MockAdapter(axios);
        const data = {
            results: [
                {
                    id: 1,
                    name: 'deluxe',
                    displayName: 'deluxe bed',
                    description: 'DLX'
                },
                {
                    id: 2,
                    name: 'luxury',
                    displayName: 'luxury bed',
                    description: 'LXY'
                },
                {
                    id: 3,
                    name: 'dbl',
                    displayName: 'double bed',
                    description: 'deluxe'
                }
            ]
        };

        mock.onGet('https://192.168.33.10/openmrs/ws/rest/v1/bedtype').reply(200, data);
    });

    it('Should render bed Type page properly', async () => {
        let bedTypeWrapper = shallow(<BedTypeWrapper match={testProps.match} />);
        const bedTypeFunctions = bedTypeWrapper.instance().bedTypeFunctions;

        await testProps.sleep(100);
        expect(bedTypeWrapper.find('BedTypeList').length).toBe(1);
        expect(shallowToJson(bedTypeWrapper)).toMatchSnapshot();

        bedTypeFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTypeId: null
            }
        });

        bedTypeWrapper.update();
        expect(bedTypeWrapper.find('AddEditBedType').length).toBe(1);
        expect(shallowToJson(bedTypeWrapper)).toMatchSnapshot();
    });

    it('Should work functions properly', async () => {
        const bedTypeWrapper = shallow(<BedTypeWrapper match={testProps.match} />);
        const bedTypeFunctions = bedTypeWrapper.instance().bedTypeFunctions;

        expect(bedTypeFunctions.getBedTypes()).toEqual([]);

        await testProps.sleep(100);
        expect(bedTypeFunctions.getBedTypes()).toEqual([
            {
                id: 1,
                name: 'deluxe',
                displayName: 'deluxe bed',
                description: 'DLX'
            },
            {
                id: 2,
                name: 'luxury',
                displayName: 'luxury bed',
                description: 'LXY'
            },
            {
                id: 3,
                name: 'dbl',
                displayName: 'double bed',
                description: 'deluxe'
            }
        ]);

        expect(bedTypeFunctions.getBedTypeById(2)).toEqual({
            id: 2,
            name: 'luxury',
            displayName: 'luxury bed',
            description: 'LXY'
        });

        expect(bedTypeFunctions.getBedTypeName('deluxe')).toEqual({
            description: 'DLX',
            displayName: 'deluxe bed',
            id: 1,
            name: 'deluxe'
        });

        bedTypeFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTypeId: null
            }
        });

        expect(bedTypeWrapper.state().activePage).toEqual('addEdit');
        expect(bedTypeWrapper.state().pageData).toEqual({
            operation: 'add',
            bedTypeId: null
        });
    });
});
