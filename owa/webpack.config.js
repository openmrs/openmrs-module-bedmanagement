const path = require('path');
const webpack = require('webpack');

const config = {
    resolve: {
        modules: [path.resolve('./app'), path.resolve('./node_modules')]
    },
    entry:{
        vendor: [
            'react',
            'react-dom',
            'react-router-dom',
            'react-intl',
            'react-cookie',
            'prop-types',
            'axios',
            'lodash',
            'babel-polyfill',
            'react-notify'
        ],
        app: ['./app/index.js']
    },
    output: {
        path: path.resolve(__dirname, 'app/build'),
        filename: '[name].js'
    },
    module: {
        rules: [{
            test: /\.js$/,
            exclude: /node_modules/,
            use: {
                'loader': 'babel-loader',
                options: {
                    presets: ['react', 'env', 'stage-2']
                }
            }
        },
        {
            test: /\.css$/,
            use: ['style-loader','css-loader' ]
        }]
    },
    plugins: [
        new webpack.optimize.CommonsChunkPlugin({
            name: 'vendor'
        })
    ]
};

module.exports = config;
