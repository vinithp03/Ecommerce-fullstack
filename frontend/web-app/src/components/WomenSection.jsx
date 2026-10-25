import React from 'react'
import { useFetchItems } from './FetchItems';
import HomeItem from './HomeItem';
import Pagination from './Pagination';

const WomenSection = () => {
  const { items, currentPage, totalPages, hasNext, hasPrevious, goToPage } =
    useFetchItems("women");

  return (
    <>
      <div className='items-container'>
        {items.map((item) => <HomeItem item={item} />)}
      </div>
      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        hasNext={hasNext}
        hasPrevious={hasPrevious}
        goToPage={goToPage}
      />
    </>
  )
}

export default WomenSection