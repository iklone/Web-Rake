import React, { Component } from 'react';
import './App.css';

class App extends Component {
  render() {
    return (
      <div className="App">
        <Home data={this.state.data} />
      </div>
    );
  }
}

export default App;
