import React from 'react';
import PropTypes from 'prop-types';
import Header from '../header';

class BedTypeWrapper extends React.Component {
    constructor(props, context){
        super(props, context);
    }

    style = {
        wrapper : {
            marginTop: 30
        }
    };

    render(){
        return <div>
            <Header path={this.props.match.path}/>
            <div style={this.style.wrapper}>
                Bed Type page.
            </div>
        </div>;
    }
}

BedTypeWrapper.contextTypes = {
    store: PropTypes.object
};

export default BedTypeWrapper;