import _ from 'lodash';

import {bedTags} from 'utilities/__tests__/testData.json';

const bedTagFunctionsMock = {
    setState: (stateData) => jest.fn(),
    getBedTags: () => {
        return bedTags;
    },
    getBedTagByUuid: (bedTagUuid) => {
        return _.find(bedTags, function(bedTag) {
            return bedTag.uuid == bedTagUuid;
        });
    },
    fetchBedTags: () => jest.fn(),
    notify: (notifyType, message) => jest.fn()
};

export default bedTagFunctionsMock;
