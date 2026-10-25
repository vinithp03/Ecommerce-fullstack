import React from "react";

/**
 * Shared pagination UI rendered below product grids.
 *
 * Renders "First / Previous / [page numbers] / Next / Last" buttons. The
 * page numbers are truncated around the current page so the button row never
 * gets too wide. All state comes from the `useFetchItems` hook.
 */
const Pagination = ({
  currentPage,
  totalPages,
  hasNext,
  hasPrevious,
  goToPage,
}) => {
  if (totalPages <= 0) return null;

  // Build a compact range of page numbers (max 5 shown).
  const buildPageNumbers = () => {
    const range = [];
    const start = Math.max(0, currentPage - 2);
    const end = Math.min(totalPages - 1, start + 4);
    for (let i = start; i <= end; i++) range.push(i);
    return range;
  };

  const pageNumbers = buildPageNumbers();

  return (
    <div className="pagination">
      <button
        className="page-btn"
        onClick={() => goToPage(0)}
        disabled={!hasPrevious}
      >
        First
      </button>
      <button
        className="page-btn"
        onClick={() => goToPage(currentPage - 1)}
        disabled={!hasPrevious}
      >
        Previous
      </button>

      {pageNumbers.map((page) => (
        <button
          key={page}
          className={`page-btn ${page === currentPage ? "active" : ""}`}
          onClick={() => goToPage(page)}
        >
          {page + 1}
        </button>
      ))}

      <button
        className="page-btn"
        onClick={() => goToPage(currentPage + 1)}
        disabled={!hasNext}
      >
        Next
      </button>
      <button
        className="page-btn"
        onClick={() => goToPage(totalPages - 1)}
        disabled={!hasNext}
      >
        Last
      </button>
    </div>
  );
};

export default Pagination;