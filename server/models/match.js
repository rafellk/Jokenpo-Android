var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var schema = new Schema({
    player1: {
        type: Schema.Types.ObjectId,
        ref: 'Player',
        required: true
    },
    player2: {
        type: Schema.Types.ObjectId,
        ref: 'Player',
        required: true
    },
    player1_move: {
        enum: ['NONE','PAPER', 'ROCK', 'SCISSORS'],
    },
    player2_move: {
        enum: ['NONE','PAPER', 'ROCK', 'SCISSORS'],
    },
    playing: {
        type: Boolean,
        required: true,
        default: false
    },
    winner: {
        type: Schema.Types.ObjectId,
        ref: 'Player',
    },
    created_at: {
        type: Date,
        default: Date.now
    },
});

module.exports = mongoose.model('Match', schema)