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
        this.deleteBedLayoutClickHandler = this.deleteBedLayoutClickHandler.bind(this);
        this.loadAdmissionLocationLayout = this.loadAdmissionLocationLayout.bind(this);
        if (props.activeUuid != null) this.loadAdmissionLocationLayout(props.activeUuid);
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
            pageData: {
                row: this.state.layoutRow != 0 ? this.state.layoutRow : 1,
                column: this.state.layoutColumn != 0 ? this.state.layoutColumn : 1
            },
            activeUuid: this.props.activeUuid
        });
    }

    deleteBedLayoutClickHandler() {
        const self = this;
        const parameters = {
            bedLayout: {
                row: 0,
                column: 0
            }
        };
        const confirmation = confirm('Are you sure you want to delete bed layout?');
        if (confirmation) {
            axios({
                method: 'post',
                url: this.urlHelper.apiBaseUrl() + '/admissionLocation/' + this.props.activeUuid,
                params: {
                    v: 'layout'
                },
                headers: {'Content-Type': 'application/json'},
                data: parameters
            })
                .then(function(response) {
                    self.props.admissionLocationFunctions.notify('success', 'Admission location bed layout deleted');

                    self.loadAdmissionLocationLayout(self.props.activeUuid);
                })
                .catch(function(errorResponse) {
                    const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                    self.props.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
                });
        }
    }

    loadAdmissionLocationLayout(locationUuid) {
        const self = this;
        axios
            .get(this.urlHelper.apiBaseUrl() + '/admissionLocation/' + locationUuid, {
                params: {
                    v: 'layout'
                }
            })
            .then(function(response) {
                let layoutRow = 0;
                let layoutColumn = 0;
                const bedlayouts = _.reduce(
                    response.data.bedLocationMappings,
                    (bedlayouts, curr) => {
                        if (typeof bedlayouts[curr.rowNumber] == 'undefined') bedlayouts[curr.rowNumber] = [];
                        if (curr.rowNumber > layoutRow) layoutRow = curr.rowNumber;
                        if (curr.columnNumber > layoutColumn) layoutColumn = curr.columnNumber;

                        bedlayouts[curr.rowNumber][curr.columnNumber] = curr;
                        return bedlayouts;
                    },
                    []
                );

                self.setState({
                    layoutRow: layoutRow,
                    layoutColumn: layoutColumn,
                    bedlayouts: bedlayouts,
                    loadingData: false
                });
            })
            .catch(function(errorResponse) {
                self.setState({
                    loadingData: false
                });

                const error = errorResponse.response.data ? errorResponse.response.data.error : errorResponse;
                self.props.admissionLocationFunctions.notify('error', error.message.replace(/\[|\]/g, ''));
            });
    }

    getBody() {
        if (this.state.loadingData == false && this.state.bedlayouts.length == 0) {
            return (
                <div className="location option">
                    <label className="button" onClick={this.addWardClickHandler}>
                        Add Child Admission Location
                    </label>
                    <label className="button" onClick={this.setBedLayoutClickHandler}>
                        Set Bed Layout
                    </label>
                </div>
            );
        } else {
            return (
                <div className="bed-layout">
                    <div className="location option">
                        <label className="button" onClick={this.setBedLayoutClickHandler}>
                            Edit Bed Layout
                        </label>
                        <label className="button" onClick={this.deleteBedLayoutClickHandler}>
                            Delete Bed Layout
                        </label>
                    </div>
                    {this.state.bedlayouts.map((rowBeds, row) => (
                        <BedLayoutRow
                            key={row}
                            layoutRow={this.state.layoutRow}
                            layoutColumn={this.state.layoutColumn}
                            row={row}
                            rowBeds={rowBeds}
                            admissionLocationFunctions={this.props.admissionLocationFunctions}
                            loadAdmissionLocationLayout={this.loadAdmissionLocationLayout}
                        />
                    ))}
                </div>
            );
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
