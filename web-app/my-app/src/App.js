import React, { Component } from 'react';
import Home from "./pages/Home";

class App extends Component {
  render() {
    return (
      <div className="App">
        <Home data={this.state} />
      </div>
    );
  }
}

export default App;
