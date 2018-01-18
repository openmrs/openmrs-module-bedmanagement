import React from 'react';
import PropTypes from 'prop-types';

import BedBlock from 'components/admissionLocation/rightPanel/bedLayout/bedBlock';

export default class BedLayoutRow extends React.Component {
    constructor(props) {
        super(props);

        this.initData = this.initData.bind(this);
        this.initData(props);
    }

    componentWillUpdate(nextProps, nextState) {
        this.initData(nextProps);
    }

    initData(props) {
        this.rowBeds = [];
        for (let col = 1; col <= props.layoutColumn; col++) {
            if (typeof props.rowBeds[col] == 'undefined') {
                this.rowBeds[col] = {
                    bedId: null,
                    bedNumber: null,
                    bedType: null,
                    bedUuid: null,
                    columnNumber: col,
                    rowNumber: props.row,
                    status: null
                };
            } else {
                this.rowBeds[col] = props.rowBeds[col];
            }
        }
    }

    render() {
        return (
            <div className="bed-layout-row">
                {' '}
                {this.rowBeds.map((bed, key) => (
                    <BedBlock
                        key={key}
                        bed={bed}
                        layoutRow={this.props.layoutColumn}
                        layoutColumn={this.props.layoutColumn}
                        admissionLocationFunctions={this.props.admissionLocationFunctions}
                        loadAdmissionLocationLayout={this.props.loadAdmissionLocationLayout}
                    />
                ))}
            </div>
        );
    }
}

BedLayoutRow.propTypes = {
    layoutRow: PropTypes.number.isRequired,
    layoutColumn: PropTypes.number.isRequired,
    row: PropTypes.number.isRequired,
    rowBeds: PropTypes.array.isRequired,
    loadAdmissionLocationLayout: PropTypes.func.isRequired,
    admissionLocationFunctions: PropTypes.object.isRequired
};
