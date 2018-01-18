import React from 'react';
import {shallow, mount} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';

import BedLayout from 'components/admissionLocation/rightPanel/bedLayout';
import admissionLocationFunctions from 'components/__mocks__/admissionLocationFunctions-mock';

require('babel-polyfill');
require('components/__mocks__/location-mock');
const testProps = {
    admissionLocationFunctions: admissionLocationFunctions,
    sleep: (milliSec) => {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                return resolve(true);
            }, milliSec);
        });
    }
};

describe('BedLayout', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            ward: {
                uuid: 'bb0e512e-d225-11e4-9c67-080027b662ec',
                display: 'VIP-100',
                name: 'VIP-100',
                description: 'VIP room 100'
            },
            bedLocationMappings: [
                {
                    rowNumber: 1,
                    columnNumber: 1,
                    bedNumber: 'vip-100',
                    bedId: 52,
                    bedUuid: '30dfc421-48ef-427f-9a67-001d2f75b8c5',
                    status: 'AVAILABLE',
                    bedType: {
                        id: 1,
                        name: 'deluxe',
                        displayName: 'deluxe bed',
                        description: 'DLXdfdfds'
                    }
                },
                {
                    rowNumber: 1,
                    columnNumber: 2,
                    bedNumber: null,
                    bedId: null,
                    bedUuid: null,
                    status: null,
                    bedType: null
                },
                {
                    rowNumber: 2,
                    columnNumber: 1,
                    bedNumber: null,
                    bedId: null,
                    bedUuid: null,
                    status: null,
                    bedType: null
                }
            ]
        };

        mock
            .onGet('https://192.168.33.10/openmrs/ws/rest/v1/admissionLocation/bb0e512e-d225-11e4-9c67-080027b662ec')
            .reply(200, data);
    });

    it('Should render bed block properly', async () => {
        const bedLayout = shallow(
            <BedLayout
                activeUuid="bb0e512e-d225-11e4-9c67-080027b662ec"
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );
        await testProps.sleep(100);
        bedLayout.update();

        expect(bedLayout.find('BedLayoutRow').length).toBe(2);
        expect(bedLayout.state()).toEqual({
            bedlayouts: [
                undefined,
                [
                    undefined,
                    {
                        bedId: 52,
                        bedNumber: 'vip-100',
                        bedType: {
                            description: 'DLXdfdfds',
                            displayName: 'deluxe bed',
                            id: 1,
                            name: 'deluxe'
                        },
                        bedUuid: '30dfc421-48ef-427f-9a67-001d2f75b8c5',
                        columnNumber: 1,
                        rowNumber: 1,
                        status: 'AVAILABLE'
                    },
                    {
                        bedId: null,
                        bedNumber: null,
                        bedType: null,
                        bedUuid: null,
                        columnNumber: 2,
                        rowNumber: 1,
                        status: null
                    }
                ],
                [
                    undefined,
                    {
                        bedId: null,
                        bedNumber: null,
                        bedType: null,
                        bedUuid: null,
                        columnNumber: 1,
                        rowNumber: 2,
                        status: null
                    }
                ]
            ],
            layoutColumn: 2,
            layoutRow: 2,
            loadingData: false
        });
        expect(shallowToJson(bedLayout)).toMatchSnapshot();

        const notYetSetBedLayout = shallow(
            <BedLayout
                activeUuid="baf83667-d225-11e4-as58-080027b662ec"
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );
        await testProps.sleep(100);
        notYetSetBedLayout.update();

        expect(shallowToJson(notYetSetBedLayout)).toMatchSnapshot();
    });

    it('Should trigger event handler', async () => {
        const sypAddWardClickHandler = jest.spyOn(BedLayout.prototype, 'addWardClickHandler');
        const sypSetBedLayoutClickHandler = jest.spyOn(BedLayout.prototype, 'setBedLayoutClickHandler');
        const spyOnSetState = jest.spyOn(testProps.admissionLocationFunctions, 'setState');
        const notYetSetBedLayout = mount(
            <BedLayout
                activeUuid="baf83667-d225-11e4-as58-080027b662ec"
                admissionLocationFunctions={testProps.admissionLocationFunctions}
            />
        );
        await testProps.sleep(100);
        notYetSetBedLayout.update();

        notYetSetBedLayout
            .find('label')
            .at(0)
            .simulate('click');
        expect(sypAddWardClickHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'addEditLocation',
            pageData: {
                operation: 'add'
            },
            activeUuid: 'baf83667-d225-11e4-as58-080027b662ec'
        });

        notYetSetBedLayout
            .find('label')
            .at(1)
            .simulate('click');
        expect(sypSetBedLayoutClickHandler).toHaveBeenCalled();
        expect(spyOnSetState).toHaveBeenCalledWith({
            activePage: 'set-layout',
            pageData: {},
            activeUuid: 'baf83667-d225-11e4-as58-080027b662ec'
        });
    });
});
