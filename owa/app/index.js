import React from 'react';
import ReactDOM from 'react-dom';
import {addLocaleData} from 'react-intl';
import App from './components/app';
import {BrowserRouter} from 'react-router-dom';

import en from 'react-intl/locale-data/en';
import es from 'react-intl/locale-data/es';
import fr from 'react-intl/locale-data/fr';
import it from 'react-intl/locale-data/it';
import pt from 'react-intl/locale-data/pt';
addLocaleData([...en, ...es, ...fr, ...it, ...pt]);

ReactDOM.render(
    <BrowserRouter>
        <App />
    </BrowserRouter>,
    document.getElementById('app')
);
