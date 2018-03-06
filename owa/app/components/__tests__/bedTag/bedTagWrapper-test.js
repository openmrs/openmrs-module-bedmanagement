import React from 'react';
import {shallow} from 'enzyme';
import {shallowToJson} from 'enzyme-to-json';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {IntlProvider} from 'react-intl';

import BedTagWrapper from 'components/bedTag/bedTagWrapper';
import messages from 'i18n/messages';

require('components/__mocks__/location-mock');
require('babel-polyfill');

const intlProvider = new IntlProvider({locale: 'en', messages: messages['en']}, {});
const {intl} = intlProvider.getChildContext();
const testData = {
    props: {
        match: {
            isExact: true,
            params: {},
            path: '/owa/bedmanagement/bedTags.html',
            url: '/owa/bedmanagement/bedTags.html'
        }
    },
    context: {
        intl: intl
    },
    sleep: (milisec) => {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                return resolve(true);
            }, milisec);
        });
    }
};

describe('BedTagWrapper', () => {
    beforeAll(() => {
        var mock = new MockAdapter(axios);
        const data = {
            results: [
                {
                    id: 2,
                    name: 'Broken',
                    uuid: 'e67ce6b1-8382-4514-80db-6d67a6611534'
                },
                {
                    id: 3,
                    name: 'Oxygen',
                    uuid: '25d9b2c8-7b70-48a5-a033-1051dcf7cc8a'
                },
                {
                    id: 4,
                    name: 'Isolation',
                    uuid: 'ff7ed494-7b9c-4478-812a-5187e297f94c'
                },
                {
                    id: 5,
                    name: 'Sanitation required',
                    uuid: '39376170-c020-4e19-9960-c6f9044886fd'
                }
            ]
        };

        mock.onGet('https://192.168.33.10/openmrs/ws/rest/v1/bedTag').reply(200, data);
    });

    it('Should render bed Tag page properly', async () => {
        let bedTagWrapper = shallow(<BedTagWrapper match={testData.props.match} />, {context: testData.context});
        const bedTagFunctions = bedTagWrapper.instance().bedTagFunctions;

        await testData.sleep(100);
        expect(bedTagWrapper.find('BedTagList').length).toBe(1);
        expect(shallowToJson(bedTagWrapper)).toMatchSnapshot();

        bedTagFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTagId: null
            }
        });

        bedTagWrapper.update();
        expect(bedTagWrapper.find('AddEditBedTag').length).toBe(1);
        expect(shallowToJson(bedTagWrapper)).toMatchSnapshot();
    });

    it('Should work functions properly', async () => {
        const bedTagWrapper = shallow(<BedTagWrapper match={testData.props.match} />, {context: testData.context});
        const bedTagFunctions = bedTagWrapper.instance().bedTagFunctions;

        expect(bedTagFunctions.getBedTags()).toEqual([]);

        await testData.sleep(100);
        expect(bedTagFunctions.getBedTags()).toEqual([
            {
                id: 2,
                name: 'Broken',
                uuid: 'e67ce6b1-8382-4514-80db-6d67a6611534'
            },
            {
                id: 3,
                name: 'Oxygen',
                uuid: '25d9b2c8-7b70-48a5-a033-1051dcf7cc8a'
            },
            {
                id: 4,
                name: 'Isolation',
                uuid: 'ff7ed494-7b9c-4478-812a-5187e297f94c'
            },
            {
                id: 5,
                name: 'Sanitation required',
                uuid: '39376170-c020-4e19-9960-c6f9044886fd'
            }
        ]);

        expect(bedTagFunctions.getBedTagByUuid('e67ce6b1-8382-4514-80db-6d67a6611534')).toEqual({
            id: 2,
            name: 'Broken',
            uuid: 'e67ce6b1-8382-4514-80db-6d67a6611534'
        });

        expect(bedTagFunctions.getBedTagByUuid('ff7ed494-7b9c-4478-812a-5187e297f94c')).toEqual({
            id: 4,
            name: 'Isolation',
            uuid: 'ff7ed494-7b9c-4478-812a-5187e297f94c'
        });

        bedTagFunctions.setState({
            activePage: 'addEdit',
            pageData: {
                operation: 'add',
                bedTagId: null
            }
        });

        expect(bedTagWrapper.state().activePage).toEqual('addEdit');
        expect(bedTagWrapper.state().pageData).toEqual({
            operation: 'add',
            bedTagId: null
        });
    });
});
