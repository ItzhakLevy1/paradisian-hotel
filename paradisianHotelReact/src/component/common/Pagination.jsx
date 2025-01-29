import React from 'react';

const Pagination = ({ roomsPerPage, totalRooms, currentPage, paginate }) => {
  const pageNumbers = [];

  for (let i = 1; i <= Math.ceil(totalRooms / roomsPerPage); i++) {
    pageNumbers.push(i);
  }

  return (
    <div className='pagination-nav'>
      <ul className="pagination-ul">
        {pageNumbers.map((number) => (
          <li key={number} className="pagination-li">
            <button onClick={() => paginate(number)} className={`pagination-button ${currentPage === number ? 'current-page' : ''}`}>
              {number}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Pagination;



/* 
The component accepts four props:

roomsPerPage: The number of rooms displayed on each page.
totalRooms: The total number of rooms available.
currentPage: The currently active page number.
paginate: A callback function to change the current page when a user clicks a page number.



Calculating Page Numbers:

Math.ceil(totalRooms / roomsPerPage) calculates the total number of pages by dividing the total rooms by the number of rooms per page and rounding up to the nearest integer.
A for loop is used to populate the pageNumbers array with numbers starting from 1 up to the total number of pages.
For example:

If there are 50 rooms and 10 rooms per page, Math.ceil(50 / 10) results in 5 pages.
The pageNumbers array would be [1, 2, 3, 4, 5].
*/