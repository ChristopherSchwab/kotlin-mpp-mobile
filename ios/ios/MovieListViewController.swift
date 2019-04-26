//
//  MovieListViewController.swift
//  ios
//
//  Created by Christopher Schwab on 25.04.19.
//  Copyright © 2019 Christopher Schwab. All rights reserved.
//

import common
import UIKit

private let reuseIdentifier = "movieViewItemCellReuseIdentifier"

class MovieListViewController: UICollectionViewController, UICollectionViewDelegateFlowLayout, MovieListView {
   
   private var movies = [MovieViewItem]()
   private var movieListController: MovieListController?
   
   private var spacing: CGFloat = 10.0
   
   required init?(coder aDecoder: NSCoder) {
      super.init(coder: aDecoder)
   }
   
   override func viewDidLoad() {
      super.viewDidLoad()

      self.collectionView.backgroundColor = UIColor(red: 0.89, green: 0.89, blue: 0.89, alpha: 1)
      self.collectionView.delegate = self
      self.collectionView!.register(UINib(nibName: "MovieViewItemCell", bundle: nil), forCellWithReuseIdentifier: reuseIdentifier)
      
      movieListController = MovieListController(
         movieListInteractor: MovieListInteractor(
            movieListPresenter: MovieListPresenter(view: self),
            httpRequestSerializer: HttpClientHttpRequestSerializer(),
            tmDbApiHost: "api.themoviedb.org",
            tmDbApiKey: "0a055ad296b0a5d7496d9a0f0cb2a7b0"
         )
      )
      
      movieListController?.onCreate()
   }
   
   func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
      return UIEdgeInsets(top: spacing, left: spacing, bottom: spacing, right: spacing)
   }
   
   func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
      return spacing
   }
   
   func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
      return spacing
   }
   
   override func numberOfSections(in collectionView: UICollectionView) -> Int {
      return 1
   }
   
   override func collectionView(_: UICollectionView, numberOfItemsInSection section: Int) -> Int {
      return movies.count
   }
   
   override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
      let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as! MovieViewItemCell
      
      //Credits: Lal Krishna (https://stackoverflow.com/questions/18113872/uicollectionviewcell-with-rounded-corners-and-drop-shadow-not-working)
      cell.contentView.layer.masksToBounds = true
      cell.contentView.layer.cornerRadius = 6
      cell.layer.shadowColor = UIColor.lightGray.cgColor
      cell.layer.shadowOffset = CGSize(width: 0, height: 2)
      cell.layer.shadowRadius = 2.0
      cell.layer.shadowOpacity = 1.0
      cell.layer.masksToBounds = false
      cell.layer.shadowPath = UIBezierPath(roundedRect: cell.bounds, cornerRadius: cell.contentView.layer.cornerRadius).cgPath
      
      cell.present(movies[indexPath.row])
      
      return cell
   }

   override func scrollViewDidScroll(_ scrollView: UIScrollView) {
      if scrollView.contentOffset.y + 1 >= (scrollView.contentSize.height - scrollView.frame.size.height) && scrollView.contentSize != .zero {
         movieListController?.onPageEnd()
      }
   }
   
   func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
      let itemWidth = (collectionView.frame.width - 3*spacing) / 2
      
      return CGSize(width: itemWidth, height: itemWidth * (40.0/27.0) + 48)
   }
   
   func showMovieViewItems(movieViewItems: [MovieViewItem]) {
      let indexPaths = Array(movies.count ..< movies.count + movieViewItems.count).map {
         IndexPath(item: $0, section: 0)
      }
      movies.append(contentsOf: movieViewItems)
      collectionView.performBatchUpdates({
         collectionView.insertItems(at: indexPaths)
      }, completion: nil)
   }
   
   func showError(errorMessage: String) {
      
   }
}
