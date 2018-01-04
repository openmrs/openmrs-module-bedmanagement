import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import _ from 'lodash';

import UrlHelper from 'utilities/urlHelper';
import BedLayoutRow from 'components/admissionLocation/rightPanel/bedLayout/bedLayoutRow';

require('./bedlayout.css');
export default class BedLayout extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            layoutRow: 0,
            layoutColumn: 0,
            bedlayouts: [],
            loadingData: true
        };

        this.urlHelper = new UrlHelper();
        this.getBody = this.getBody.bind(this);
        this.setBedLayoutClickHandler = this.setBedLayoutClickHandler.bind(this);
        this.addWardClickHandler = this.addWardClickHandler.bind(this);
        this.loadAdmissionLocationLayout = this.loadAdmissionLocationLayout.bind(this);
        if (props.activeUuid != null)
            this.loadAdmissionLocationLayout(props.activeUuid);
    }

    componentWillUpdate(nextProps, nextState) {
        if (this.props.activeUuid != nextProps.activeUuid) {
            if (nextProps.activeUuid != null) {
                this.loadAdmissionLocationLayout(nextProps.activeUuid);
            } else {
                this.setState({
                    layoutRow: 0,
                    layoutColumn: 0,
                    bedlayouts: [],
                    loadingData: false
                });
            }

        }
    }

    addWardClickHandler() {
        this.props.admissionLocationFunctions.setState({
            activePage: 'addEditLocation',
            pageData: {
                operation: 'add'
            },
            activeUuid: this.props.activeUuid
        });
    }

    setBedLayoutClickHandler() {
        this.props.admissionLocationFunctions.setState({
            activePage: 'set-layout',
            pageData: {},
            activeUuid: this.props.activeUuid
        });
    }

    loadAdmissionLocationLayout(locationUuid) {
        const self = this;
        axios.get(this.urlHelper.apiBaseUrl() + '/admissionLocation/' + locationUuid, {
            params: {
                v: 'layout'
            }
        }).then(function (response) {
            let layoutRow = 0;
            let layoutColumn = 0;
            const bedlayouts = _.reduce(response.data.bedLocationMappings, (bedlayouts, curr) => {
                if (typeof bedlayouts[curr.rowNumber] == 'undefined')
                    bedlayouts[curr.rowNumber] = [];
                if (curr.rowNumber > layoutRow)
                    layoutRow = curr.rowNumber;
                if (curr.columnNumber > layoutColumn)
                    layoutColumn = curr.columnNumber;

                bedlayouts[curr.rowNumber][curr.columnNumber] = curr;
                return bedlayouts;
            }, []);

            self.setState({
                layoutRow: layoutRow,
                layoutColumn: layoutColumn,
                bedlayouts: bedlayouts,
                loadingData: false
            });
        }).catch(function (error) {
            self.setState({
                loadingData: false
            });
            self.props.admissionLocationFunctions.notify('error', error.message);
        });
    }

    getBody() {
        if (this.state.loadingData == false && this.state.bedlayouts.length == 0) {
            return <div className="location option">
                <label className="btn btn-primary" onClick={this.addWardClickHandler}>Add Child Admission
                    Location</label>
                <label className="btn btn-primary" onClick={this.setBedLayoutClickHandler}>Set Bed Layout</label>
            </div>;
        } else {
            return <div className="bed-layout">
                {this.state.bedlayouts.map((rowBeds, row) =>
                    <BedLayoutRow key={row} layoutRow={this.state.layoutRow} layoutColumn={this.state.layoutColumn}
                        row={row} rowBeds={rowBeds} admissionLocationFunctions={this.props.admissionLocationFunctions}
                        loadAdmissionLocationLayout={this.loadAdmissionLocationLayout}/>)}
            </div>;
        }
    }

    render() {
        return this.getBody();
    }
}

BedLayout.propTypes = {
    activeUuid: PropTypes.string,
    admissionLocationFunctions: PropTypes.object.isRequired
};